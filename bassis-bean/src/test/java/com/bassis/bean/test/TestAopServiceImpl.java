package com.bassis.bean.test;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.aop.AopService;
import com.bassis.bean.test.service.TestService1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TestAopServiceImpl implements AopService {
    static Logger logger = LoggerFactory.getLogger(TestAopServiceImpl.class);
    @Autowired
    TestService1 testService1;
    @Override
    public boolean preHandle(Object... objs) {
        testService1.out();
        logger.info("preHandle");
        for (Object obj : objs) {
            logger.info("aop入参:" + obj.toString());
        }
        return true;
    }

    @Override
    public void postHandle(Object... objs) {
        logger.info("postHandle");
    }

    @Override
    public void afterCompletion(Object... objs) {
        logger.info("afterCompletion");
    }
}
