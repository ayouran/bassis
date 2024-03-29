package org.bs.service.impl;

import com.bassis.bean.annotation.Aop;
import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.event.ApplicationEventPublisher;
import org.bs.db.UserDb;
import org.bs.event.TestEvent;
import org.bs.service.UserService;
import org.bs.vo.JsonVO;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    UserDb userDb;

    // 以下任意一种使用方式均可
    //        @Aop(aclass = UserAopServiceImpl.class)
//        @Aop(aclass = UserAopServiceImpl.class, parameters = {"a", "b", "c"})
//    @Aop(value = "userAopService", parameters = {"a", "b", "c"})
    @Aop(value = "userAopService")
    @Override
    public String add(String ds, String ds2) {
        ApplicationEventPublisher.publishEvent(new TestEvent(new Object()));
        return userDb.add(ds) + "  |  " + userDb.add(ds2) + "  " + this.getClass().getSimpleName();
    }

    @Override
    public JsonVO json(JsonVO jsonVO) {
        return jsonVO;
    }
}
