package com.bassis.bean.annotation.impl;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.annotation.Listener;
import com.bassis.bean.common.Bean;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.ApplicationListener;
import com.bassis.bean.event.domain.listener.AutowiredEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理自定义事件监听注解
 *
 * @see Listener
 */
public class ListenerImpl {
    private static Logger logger = LoggerFactory.getLogger(ListenerImpl.class);
    private static BeanFactory beanFactory = BeanFactory.getInstance();

    private static class LazyHolder {
        private static final ListenerImpl INSTANCE = new ListenerImpl();
    }

    private ListenerImpl() {
    }

    public static ListenerImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    static {
        //必须第一时间手动绑定全局资源注入监听
        ApplicationEventPublisher.addListener(new AutowiredEventListener());
    }

    public void listener() {
        beanFactory.getClaMapFirstBean().forEach((key, value) -> {
            Listener listener = key.getAnnotation(Listener.class);
            if (null == listener) return;
            Bean bean = value;
            if (bean == null) bean = beanFactory.createBean(key);
            if (bean != null && bean.getObject() != null) {
                logger.info("add Listener :{}", key);
                ApplicationEventPublisher.addListener((ApplicationListener) bean.getObject());
            }
        });
    }
}
