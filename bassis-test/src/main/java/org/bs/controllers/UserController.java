package org.bs.controllers;

import com.bassis.bean.annotation.Autowired;
import com.bassis.boot.web.annotation.Controller;
import com.bassis.boot.web.annotation.RequestMapping;
import com.bassis.boot.web.annotation.RequestParam;
import com.bassis.boot.web.common.enums.RequestMethodEnum;
import org.bs.service.UserService;

@Controller("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/add", method = {RequestMethodEnum.GET, RequestMethodEnum.POST})
    public String add(@RequestParam(required = false) String ds) {
        return userService.add(ds) + this.getClass().getSimpleName();
    }
}
