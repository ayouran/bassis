package com.bassis.bean.test.service;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.annotation.Scope;
import com.bassis.bean.common.enums.ScopeEnum;

@Scope(value = ScopeEnum.PROTOTYPE)
@Component
public class TestService3 {
    @Autowired(aclass = TestService1.class)
    TestService1 testService1;

    public String out() {
        String str = "TestService3";
        System.out.println(str + testService1.out());
        return str;
    }
    public String out2() {
        String str = "TestService3";
        System.out.println(str );
        return str;
    }
}