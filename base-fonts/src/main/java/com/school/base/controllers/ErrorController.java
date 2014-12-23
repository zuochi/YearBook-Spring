package com.school.base.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 错误页面跳转控制器
 *
 * @author work
 */
@Controller
@RequestMapping(value = "/error")
public class ErrorController {

    @RequestMapping(value = "/{errorCode}")
    public ModelAndView error(@PathVariable int errorCode) {
        switch (errorCode) {
            case 401:
                return new ModelAndView("views/shared/401");
            case 403:
                return new ModelAndView("views/shared/403");
            case 404:
                return new ModelAndView("views/shared/404");
            case 500:
                return new ModelAndView("views/shared/500");
            case 503:
                return new ModelAndView("views/shared/503");
            default:
                return new ModelAndView("views/shared/404");
        }
    }
}
