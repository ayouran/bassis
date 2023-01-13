package com.bassis.boot.web;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.common.Bean;
import com.bassis.boot.common.HttpPage;
import com.bassis.boot.web.annotation.impl.ControllerImpl;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.reflex.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servlet 核心容器
 */
public class BassisHttp {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(BassisHttp.class);
    private static final BeanFactory beanFactory = BeanFactory.getInstance();
    private static final ControllerImpl controller = ControllerImpl.getInstance();

    private static class LazyHolder {
        private static final BassisHttp INSTANCE = new BassisHttp();
    }

    private BassisHttp() {
    }

    public static BassisHttp getInstance() {
        return BassisHttp.LazyHolder.INSTANCE;
    }

    /**
     * 获取路由列表
     */
    public Set<String> getPaths() {
        return controller.getPaths();
    }

    /**
     * 执行方法
     *
     * @param requestPath       请求路径
     * @param requestParameters 请求参数
     * @return 执行结果
     * @throws Exception
     */
    public Object service(String requestPath, LinkedHashMap<String, Object> requestParameters) {
        logger.debug("初始化service资源...");
        Object resInvoke = null;
        try {
            Class<?> actionCla = ControllerImpl.getMapClass(requestPath);
            Method method = ControllerImpl.getMapMethod(requestPath);
            if (HttpPage.ERROR_500.equalsIgnoreCase(requestPath)
                    || HttpPage.ERROR_503.equalsIgnoreCase(requestPath)
                    || HttpPage.ERROR_404.equalsIgnoreCase(requestPath)) {
            } else if (null == actionCla || null == method) {
                CustomException.throwOut("Requested resource not found:{}" + requestPath);
            } else {
                logger.debug("Start processing the request :{}", requestPath);
            }
            assert method != null;
            if (method.isVarArgs())
                CustomException.throwOut("variable parameter:" + requestPath + " method:" + method.getName());
            List<Object> parameters = ControllerImpl.getMapParameter(method);
            //请求参数值，参数值类型
            assert parameters != null;
            Map<Object, Object> mapParameters = new LinkedHashMap<>(parameters.size());
            //验证方法参数
            int count = parameters.size() / 3;
            if (count <= 0) count = 1;
            for (int i = 0; i < count; i = i + 3) {
                String name = (String) parameters.get(i);
                Class<?> type = (Class<?>) parameters.get(i + 1);
                Boolean required = (Boolean) parameters.get(i + 2);
                //检查必须参数
                if (!requestParameters.containsKey(name) && required)
                    CustomException.throwOut("method required parameter : " + name + " is null [" + requestPath + "]");
                String[] ps = (String[]) requestParameters.get(name);
                if (null == ps) {
                    mapParameters.put(null, type);
                } else {
                    mapParameters.put(ps[0], type);
                }
            }
            //交由bean进行生产
            Bean bean = beanFactory.createBean(actionCla);
            resInvoke = Reflection.invokeMethod(bean.getObject(), method, mapParameters.keySet().toArray());
            logger.info("resInvoke : " + resInvoke);
            //清除资源
            beanFactory.removeBean(bean);
        } catch (Exception e) {
            CustomException.throwOut("controller [" + requestPath + "] error ", e);
        }
        logger.debug("service方法完成");
        return resInvoke;
    }
}
