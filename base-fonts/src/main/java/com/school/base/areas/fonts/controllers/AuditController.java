package com.school.base.areas.fonts.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.school.base.common.easyui.DataGridModel;
import com.school.base.common.mvc.MutilCustomDateEditor;
import com.school.base.common.web.RequestUtil;
import com.school.base.domain.management.Audit;
import com.school.base.domain.management.AuditVariable;
import com.school.base.service.face.management.IAuditService;

import java.util.Map;

/**
 * T_AUDIT( ) 控制器
 *
 * @作者 work
 * @创建时间 2012-12-11 14:58:32
 */
@Controller("management.auditController")
@RequestMapping("/management/audit")
public class AuditController {

    @Autowired
    private IAuditService auditService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new MutilCustomDateEditor("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"));
    }

    /**
     * GET 查询
     *
     * @return 视图路径
     */
    @RequestMapping(value = {"/index", ""})
    public String index() {
        return "/areas/management/views/audit/index";
    }

    /**
     * 模糊统计符合查询条件的记录总数
     *
     * @param request 查询参数
     * @return 记录总数
     */
    @ResponseBody
    @RequestMapping("/count")
    public int count(HttpServletRequest request) {
        return this.auditService.count(RequestUtil.asMap(request));
    }

    /**
     * 模糊获取符合查询条件的分页记录
     *
     * @param page 页码
     * @param rows 页长
     * @param total 符合查询条件的记录总数
     * @param request 查询参数
     * @return 记录列表
     */
    @ResponseBody
    @RequestMapping("/page")
    public DataGridModel<Audit> page(int page, int rows, HttpServletRequest request) {
        int start = (page - 1) * rows;
        int limit = rows;
        List<Audit> list = this.auditService.page(start, limit, RequestUtil.asMap(request));
        return new DataGridModel<Audit>(list, this.count(request));
    }

    /**
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/{id}/variables")
    public DataGridModel<AuditVariable> getVariables(@PathVariable int id, HttpServletRequest request) {
        Map map = RequestUtil.asMap(request);
        List<AuditVariable> list = this.auditService.getVariables(id, map);
        return new DataGridModel<AuditVariable>(list);
    }

    /**
     * 根据标识获取记录
     *
     * @param id 标识
     * @return 记录
     */
    @ResponseBody
    @RequestMapping("/load/{id}")
    public Audit load(@PathVariable("id") int id) {
        Audit audit = null;
        if (id > 0) {
            audit = this.auditService.load(id);
        }
        return audit;
    }
}
