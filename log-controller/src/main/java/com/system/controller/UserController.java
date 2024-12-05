package com.system.controller;

import com.system.po.User;
import com.system.pojo.CommonResult;
import com.system.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Ming
 * @date 2021/9/23 - 15:46
 */
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("hello")
    public CommonResult<User> hello() {
        return CommonResult.successAndReturn(userService.hello());
    }

}
