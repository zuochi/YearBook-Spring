package com.school.base.areas.fonts.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author work
 */
@Controller("management.historyController")
@RequestMapping("/management/history")
public class HistoryController {

    @RequestMapping("")
    public ModelAndView index(String tbl, Integer key) {
        ModelAndView andView = new ModelAndView();
        andView.addObject("tbl", tbl);
        andView.addObject("key", key == null ? -1 : key);
        andView.setViewName("/areas/management/views/audit/history");
        return andView;
    }
}
