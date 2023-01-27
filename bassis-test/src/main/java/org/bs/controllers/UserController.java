package org.bs.controllers;

import com.bassis.bean.annotation.Autowired;
import com.bassis.boot.web.annotation.Controller;
import com.bassis.boot.web.annotation.RequestMapping;
import com.bassis.boot.web.annotation.RequestParam;
import com.bassis.boot.web.common.enums.RequestMethodEnum;
import org.bs.service.UserService;
import org.bs.vo.JsonVO;

@Controller("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/add", method = {RequestMethodEnum.GET, RequestMethodEnum.POST})
    public String add(@RequestParam(required = false) String ds, @RequestParam(required = false) String ds2) {
        return userService.add(ds, ds2) + this.getClass().getSimpleName();
    }

    @RequestMapping(value = "/json", method = RequestMethodEnum.POST)
    public JsonVO json(@RequestParam JsonVO jsonVO) {
        return userService.json(jsonVO);
    }
}
