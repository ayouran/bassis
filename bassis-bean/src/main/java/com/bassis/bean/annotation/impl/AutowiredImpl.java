package com.bassis.bean.annotation.impl;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.common.Bean;
import com.bassis.bean.common.FieldBean;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.reflex.ReflexUtils;
import com.bassis.tools.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 对当前所有资源的自动注入进行实现
 *
 * @see Autowired
 */
public class AutowiredImpl {
    private final static Logger logger = LoggerFactory.getLogger(AutowiredImpl.class);
    private final BeanFactory beanFactory = BeanFactory.getInstance();
    private final List<FieldBean> fieldBeans = new CopyOnWriteArrayList<>();

    private static class LazyHolder {
        private static final AutowiredImpl INSTANCE = new AutowiredImpl();
    }

    private AutowiredImpl() {
    }

    public static AutowiredImpl getInstance() {
        return AutowiredImpl.LazyHolder.INSTANCE;
    }

    /**
     * 全局字段注解分析
     *
     * @param object     当前类
     * @param superClass 是否从父类获取字段
     */
    public void analyseFields(Object object, boolean superClass) {
        Field[] fields;
        if (superClass) {
            fields = object.getClass().getSuperclass().getDeclaredFields();
        } else {
            fields = object.getClass().getDeclaredFields();
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                fieldAutowired(object, field);
            }
        }
    }

    /**
     * 字段属性注入
     * 这个方法不支持dao注入
     *
     * @param obj   当前类
     * @param field 要注入的字段
     */
    private void fieldAutowired(Object obj, Field field) {
        String position = "[fieldAutowired] bean:" + obj.getClass().getName() + " field:" + field.getName();
        try {
            field.setAccessible(true);
            Class<?> cla = field.getType();
            Autowired annotation = field.getAnnotation(Autowired.class);
            // 输出注解上的属性
            String value = annotation.value();
            Class<?> aclass = annotation.aclass();
            Class fieldClass = null;
            //优先从注解属性中获取
            if (!ReflexUtils.isWrapClass(cla.getName())) {
                //不是基础类型
                if (!StringUtils.isEmptyString(value)) fieldClass = beanFactory.getBeansClass(value);
                if (null == fieldClass && !aclass.isAssignableFrom(Autowired.class))
                    fieldClass = beanFactory.getBeansClass(aclass);
            }
            //注解中获取不到时从默认关系中获取
            if (null == fieldClass) fieldClass = beanFactory.getComponentClass(cla);
            if (null != fieldClass) {
                //放入当前注入对象任务区，等待循环依赖资源初始化完成
                fieldBeans.add(new FieldBean(obj, field, fieldClass));
                //根据fieldClass 向beanFactory提交一个创建bean的任务，如果任务完成会通知所有关联的注入对象进行资源注入
                beanFactory.newBeanTask(fieldClass);
                logger.debug(position + " 字段注入任务创建成功");
            } else logger.warn(position + " 没有找到可用资源");
        } catch (Exception e) {
            logger.error(position + " 字段参数注入失败", e);
        }
    }

    /**
     * 执行注入
     */
    public void twoStageAutowired() {
        this.fieldBeans.forEach(this::fieldBeanAutowired);
    }

    /**
     * 单个bean注入
     *
     * @param fieldBean 要操作的fieldBean
     */
    private void fieldBeanAutowired(FieldBean fieldBean) {
        String position = "[twoStageAutowired] bean: " + fieldBean.getObject().getClass().getName() + "field:" + fieldBean.getField().getName();
        Bean bean = beanFactory.createBean(fieldBean.getFieldClass());
        if (null == bean) CustomException.throwOut(position + " @Autowired not resource bean");
        assert bean != null;
        Object fieldObject = bean.getObject();
        if (null == fieldObject) CustomException.throwOut(position + " @Autowired not resource object");
        try {
            fieldBean.getField().set(fieldBean.getObject(), fieldObject);
        } catch (IllegalAccessException e) {
            logger.error(position + " 字段参数注入失败", e);
        }
        logger.debug(position + " 字段参数注入成功");
        fieldBeans.remove(fieldBean);
    }
}
