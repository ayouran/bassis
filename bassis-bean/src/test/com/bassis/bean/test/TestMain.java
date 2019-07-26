package com.bassis.bean.test;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.CachedBeanCopier;
import com.bassis.bean.annotation.impl.AutowiredImpl;
import com.bassis.bean.common.Bean;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.domain.AutowiredEvent;
import com.bassis.bean.proxy.CglibProxy;
import com.bassis.bean.proxy.ProxyFactory;
import com.bassis.bean.test.service.TestService1;
import com.bassis.bean.test.service.TestService2;
import com.bassis.bean.test.service.TestService3;
import com.bassis.tools.reflex.Reflection;
import javafx.scene.media.SubtitleTrack;
import net.sf.cglib.beans.BeanCopier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestMain {
    static BeanFactory beanFactory;

    public static void main(String[] args) throws Exception {
        beanFactory = BeanFactory.startBeanFactory("com.bassis.bean.test");
//        TestProxy testProxy = (TestProxy) beanFactory.getBean(TestProxy.class).getObject();
//        String res = testProxy.tp();
//        System.out.println(res);

//        TestService1 service1 = (TestService1) beanFactory.getBeanFirst(TestService1.class).getObject();
//        service1.out();
//        TestService2 service2 = (TestService2) beanFactory.getBeanFirst(TestService2.class).getObject();
//        service2.out();
//        TestService3 service3 = (TestService3) beanFactory.getBeanFirst(TestService3.class).getObject();
//        service3.out();
//
//        System.out.println(beanFactory.getBeanList(TestService1.class).size());
//        System.out.println(beanFactory.getBeanList(TestService2.class).size());
//        System.out.println(beanFactory.getBeanList(TestService3.class).size());


//        ApplicationEventPublisher applicationEventPublisher = ApplicationEventPublisher.getInstance();
//        AutowiredImpl autowired = new AutowiredImpl();
//        applicationEventPublisher.addListener(autowired);
//        applicationEventPublisher.publishEvent(new AutowiredEvent(TestService1.class));

        testCopyBean();
    }

    private static void testCopyBean() {
        Bean source = new Bean(ProxyFactory.invoke(TestService1.class), 1);
        Bean target = new Bean();
        beanFactory.copyBean(source, target);
    }
}
