package com.school.base.controllers;

import com.school.base.common.security.IContextService;
import com.school.base.common.security.UserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/account")
public class AccountController {

    @Autowired
    private IContextService contextService;

    @RequestMapping("/login")
    public String login() {
        return "views/account/login";
    }

    @ResponseBody
    @RequestMapping("/username")
    public String username() {
        return this.contextService.getUsername();
    }

    @ResponseBody
    @RequestMapping("/userinfo")
    public UserInfo userInfo() {
        return this.contextService.getUserInfo();
    }
}
