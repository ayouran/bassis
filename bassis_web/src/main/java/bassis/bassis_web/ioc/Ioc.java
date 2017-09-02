package bassis.bassis_web.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;

import bassis.bassis_bean.BeanFactory;
import bassis.bassis_bean.ReferenceDeclaration;
import bassis.bassis_bean.annotation.Autowired;
import bassis.bassis_tools.exception.CustomException;
import bassis.bassis_tools.gc.GcUtils;
import bassis.bassis_tools.reflex.Reflection;
import bassis.bassis_tools.reflex.ReflexUtils;
import bassis.bassis_tools.string.StringUtils;
import bassis.bassis_web.assist.Resource;
import bassis.bassis_web.assist.StewardResource;

public class Ioc {
	private static Logger logger = Logger.getLogger(Ioc.class);

	/**
	 * 判断是否需要启用db ioc 为了避免依赖bassis_hibernate 这里的实现方式会用反射方式实现
	 * 
	 * @param obj
	 */
	public static void isIocDB(Object obj) {
		// 没有使用bassis_hibernate框架
		if (!ReflexUtils.isClass(ReferenceDeclaration.BASSIS_HIBERNATE)) {
			logger.warn("未使用bassis_hibernate,跳过相关设置");
			return;
		}
		try {
			Object objDB = BeanFactory.getClassLoaderToObject(ReferenceDeclaration.BASSIS_HIBERNATE);
			Method method = Reflection.getMethod(objDB.getClass(), ReferenceDeclaration.BASSIS_HIBERNATE_DBIOC);
			Reflection.invokeMethod(objDB, method, obj,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			CustomException.throwOut("exe bassis_hibernate ioc failure", e);
		}
	}

	/**
	 * 为web方式注入请求参数之外的字段自动注入
	 */
	public static void iocResource(Object obj, Map<String, Object> mapRds) throws Exception {
		Field[] fields = null;
		try {
			fields = obj.getClass().getDeclaredFields();
			if (null == fields || fields.length <= 0)
				return;

			String _name = obj.getClass().getSimpleName().toLowerCase() + ".";
			for (Field field : fields) {
				// 排除掉已经在bean中进行了ioc注入的属性
				String name = field.getName();
				_name = _name + name;
				if (!field.isAnnotationPresent(Autowired.class) || mapRds.containsKey(name)
						|| mapRds.containsKey(_name)) {
					continue;
				}
				ioc(obj, field);
			}
		} finally {
			GcUtils.getInstance();
		}
	}

	/**
	 * 字段属性注入 解决接口与Resource资源
	 * 
	 * @param clz
	 * @param field
	 */
	private static void ioc(Object obj, Field field) throws Exception {
		field.setAccessible(true);
		Class<?> cla = field.getType();
		Autowired annotation = field.getAnnotation(Autowired.class);
		// 输出注解上的属性
		String value = annotation.value();
		Class<?> clas = annotation.clas();
		Object fobj = null;
		// 只有当lcas是一个接口 并且注解有参数
		if (cla.isInterface()) {
			if (null != clas && clas != Object.class) {
				fobj = BeanFactory.getLoader(clas);
			} else if (!StringUtils.isEmptyString(value)) {
				Class<?> claImpl = BeanFactory.getClassLoader(value);
				fobj = BeanFactory.getBeanObj(claImpl);
			} else {
				CustomException.throwOut("@Autowired not resource");
			}
		} else if (field.getType() == Resource.class) {
			// 是资源类型
			fobj = StewardResource.get(obj.toString());
		}
		if (null == fobj) {
			return;
		}
		field.set(obj, fobj);
		logger.debug(field.getName() + "字段接口与Resourc资源注入成功");
	}

}
