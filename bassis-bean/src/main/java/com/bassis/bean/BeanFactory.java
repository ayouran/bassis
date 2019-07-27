package com.bassis.bean;

import com.bassis.bean.annotation.Scope;
import com.bassis.bean.annotation.impl.AutowiredImpl;
import com.bassis.bean.common.Bean;
import com.bassis.bean.common.enums.ScopeEnum;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.domain.AutowiredEvent;
import com.bassis.bean.event.domain.ResourcesEvent;
import com.bassis.bean.proxy.ProxyFactory;
import com.bassis.tools.exception.CustomException;
import net.sf.cglib.beans.BeanGenerator;
import org.apache.log4j.Logger;
import com.bassis.bean.annotation.impl.ComponentImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean工厂
 */
public class BeanFactory {

    private static Logger logger = Logger.getLogger(BeanFactory.class);

    private static class LazyHolder {
        private static final BeanFactory INSTANCE = new BeanFactory();
    }

    private BeanFactory() {
    }

    public static final BeanFactory getInstance() {
        return BeanFactory.LazyHolder.INSTANCE;
    }

    /**
     * 一级缓存：所有bean存储器
     */
    private static final Map<Class<?>, LinkedList<Bean>> objectBeanStorage = new ConcurrentHashMap<>(256);

    /**
     * 二级缓存：存放正在初始化的对象，用于解决循环依赖
     */
    private static final Map<Class<?>, Bean> singletonFactories = new ConcurrentHashMap<>(16);
    private static final String CGLIB_TAG = "$$EnhancerByCGLIB$$";

    static {
        //初始化bean拷贝器
        CachedBeanCopier.getInstance();
        //初始化全局事件组件
        ApplicationEventPublisher.getInstance();
    }

    /**
     * 启动 BeanFactory
     *
     * @param scanPath 扫描起点
     * @return 返回 BeanFactory
     */
    public static BeanFactory startBeanFactory(String scanPath) {
        //初始化扫描器
        Scanner.startScan(scanPath);
        //开始component扫描
        ComponentImpl.getInstance();
        //将剩下没有循环依赖的bean放入存储器
        getInstance().addBeanSingletonFactories();
        //发布资源就绪事件
        ApplicationEventPublisher.publishEvent(new AutowiredEvent(Object.class));
        ApplicationEventPublisher.publishEvent(new ResourcesEvent(Object.class));
        return getInstance();
    }

    /**
     * 检查class是否带有范围注解 默认为单实例
     *
     * @param aclass 要检测的class
     * @return 单实例为true 多实例为false
     */
    public static boolean isScopeSingleton(Class<?> aclass) {
        return !aclass.isAnnotationPresent(Scope.class) || !aclass.getAnnotation(Scope.class).value().equals(ScopeEnum.PROTOTYPE);
    }

    /**
     * 检查class是否存在已创建好的bean
     *
     * @param aclass 要检测的class
     * @return 已存在bean为true 否则为false
     */
    public static boolean isBean(Class<?> aclass) {
        return objectBeanStorage.containsKey(aclass);
    }

    /**
     * 创建一个bean
     *
     * @param aclass 要创建的class
     * @return 返回创建好的bean
     */
    public synchronized void newBeanTask(Class<?> aclass) {
        if (isBean(aclass)) {
            //第一阶段，检测一级缓存中是否已存在当前aclass
            //存在bean
        } else if (singletonFactories.containsKey(aclass)) {
            //第二阶段，检测二级缓存中是否已存在当前aclass
            // 存在bean 将当前bean返回
            Bean bean = singletonFactories.get(aclass);
            //将这个bean放入一级缓存
            addBean(bean);
            singletonFactories.remove(aclass);
        } else {
            //创建一个待初始化的bean放入二级缓存
            Bean bean = new Bean(ProxyFactory.invoke(aclass), 1);
            singletonFactories.put(aclass, bean);
            //检测资源注入,并且加入事件
            AutowiredImpl autowired = new AutowiredImpl();
            ApplicationEventPublisher.addListener(autowired);
            autowired.analyseFields(bean.getObject(), true);
        }
    }

    /**
     * 在所有bean创建完成之后将剩下没有循环依赖的bean放入存储器
     */
    private void addBeanSingletonFactories() {
        singletonFactories.values().forEach(this::addBean);
    }

    /**
     * 删除一个bean
     * (如果bean没有索引，不会执行bean存储器删除)
     *
     * @param bean 要移除的bean
     * @return 返回成功或失败
     */
    public synchronized boolean removeBean(Bean bean) {
        try {
            assert bean != null;
            assert bean.getObject() != null;
            Class aclass = bean.getObject().getClass();
            //删除bean存储器
            if (!isBean(aclass)) return false;
            LinkedList<Bean> beans = objectBeanStorage.get(aclass);
            if (beans.isEmpty()) return false;
            if (null != bean.getIndex() && bean.getIndex() > 0) {
                beans.remove(bean.getIndex().intValue());
                objectBeanStorage.put(aclass, beans);
                return true;
            } else {
                logger.warn("bean index is null");
            }
        } catch (Exception e) {
            CustomException.throwOut(" removeBean exception", e);
        }
        return false;
    }


    /**
     * 获得class 创建的实例列表
     *
     * @param aclass 要获得的class
     * @return 返回创建的实例列表
     */
    public LinkedList<Bean> getBeanList(Class<?> aclass) {
        LinkedList<Bean> beans = new LinkedList<>();
        if (isBean(aclass)) {
            beans = objectBeanStorage.get(aclass);
        }
        return beans;
    }

    /**
     * 增加一个bean到存储器
     *
     * @param bean 要增加的bean
     * @return 返回增加后的bean，主要是增加了索引
     */
    public Bean addBean(Bean bean) {
        Class<?> aclass = bean.getObject().getClass().getSuperclass();
        synchronized (this) {
            LinkedList<Bean> beans = this.getBeanList(aclass);
            bean.setIndex(beans.size() + 1);
            beans.add(bean);
            objectBeanStorage.put(aclass, beans);
        }
        return bean;
    }

    /**
     * 获得一个资源就绪的bean(最先创建的bean)
     *
     * @param aclass 要获得的实例
     * @return 返回获得的bean
     */
    public Bean getBeanFirst(Class<?> aclass) {
        Bean bean = null;
        if (isBean(aclass)) {
            //存在bean 直接返回第一个bean
            bean = objectBeanStorage.get(aclass).getFirst();
        }
        return bean;
    }

    /**
     * 获得一个资源就绪的bean
     *
     * @param name 要获得的实例的别名
     * @return 返回获得的bean
     */
    public Bean getBeanFirst(String name) {
        return getBeanFirst(ComponentImpl.getBeansClass(name));
    }

    /**
     * 获得一个资源就绪的bean(最后创建的bean)
     *
     * @param name 要获得的实例
     * @return 返回获得的bean
     */
    public Bean getByLastBean(String name) {
        return getByLastBean(ComponentImpl.getBeansClass(name));
    }

    /**
     * 获得一个资源就绪的bean(最后创建的bean)
     *
     * @param aclass 要获得的实例
     * @return 返回获得的bean
     */
    public Bean getByLastBean(Class<?> aclass) {
        Bean bean = null;
        if (isBean(aclass)) {
            //存在bean 直接返回第一个bean
            bean = objectBeanStorage.get(aclass).getLast();
        }
        return bean;
    }

    /**
     * 获得一个资源就绪的bean(指定索引，默认是第一个)
     *
     * @param name  要获得的实例
     * @param index 要获得的实例的索引，下标从1开始
     * @return 返回获得的bean
     */
    public Bean getByIndexBean(String name, int index) {
        return getByIndexBean(ComponentImpl.getBeansClass(name), index);
    }

    /**
     * 获得一个资源就绪的bean(指定索引，默认是第一个)
     *
     * @param aclass 要获得的实例
     * @param index  要获得的实例的索引，下标从1开始
     * @return 返回获得的bean
     */
    public Bean getByIndexBean(Class<?> aclass, int index) {
        if (index <= 1) index = 0;
        else index--;
        LinkedList<Bean> beans = this.getBeanList(aclass);
        if (beans.size() < index) CustomException.throwOut("index  Crossing the line  Beans size");
        return beans.get(index);
    }

    /**
     * 拷贝一个对象
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public void copyBean(Object source, Object target) {
        boolean sourceSuperClass = source.getClass().getName().contains(CGLIB_TAG);
        boolean targetSuperClass = target.getClass().getName().contains(CGLIB_TAG);
        CachedBeanCopier.copy(source, target, sourceSuperClass, targetSuperClass);
    }
}