package com.school.yearbook.areas.management.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.school.base.common.easyui.DataGridModel;
import com.school.base.common.web.RequestUtil;
import com.school.yearbook.domain.management.Advice;
import com.school.yearbook.service.face.management.IAdviceService;

/**
 * T_ACCOUNT(帐户) 控制器
 *
 * @作者 陈开敏
 * @创建时间 2014-05-23 10:04:07
 */
@Controller("management.accountController")
@RequestMapping("/management/advice")
public class AdviceController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private IAdviceService adviceService;

    /**
     * GET 查询
     *
     * @return 视图路径
     */
    @RequestMapping(value = {"/index", ""})
    public String index() {
        return "/areas/management/views/advice/index";
    }

    /**
     * GET 创建
     *
     * @return 视图路径
     */
    @RequestMapping("/create")
    public String create() {
        return "/areas/management/views/advice/create";
    }

    /**
     * GET 编辑
     *
     * @param id 标识
     * @return 视图路径
     */
    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id) {
        return "/areas/management/views/advice/edit";
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
        return this.adviceService.count(RequestUtil.asMap(request, false));
    }

    /**
     * 模糊获取符合查询条件的分页记录
     * 
     * @param page 页码
     * @param limit 页长
     * @param request 查询参数
     * @return 记录列表
     */
    @ResponseBody
    @RequestMapping("/page")
    public DataGridModel<Advice> page(@RequestParam(value="page")int page, @RequestParam(value="rows")int limit, HttpServletRequest request) {
    	List<Advice> advices = new ArrayList<Advice>();
    	Map<String, Object> queryParams = RequestUtil.asMap(request, false);
        int start = (page - 1) * limit;
        int total = this.adviceService.count(queryParams);
        if(total > 0) {
        	advices = this.adviceService.page(start, limit, queryParams);
        }
        return new DataGridModel<Advice>(advices, total);
    }

    /**
     * 根据标识获取记录
     *
     * @param id 标识
     * @return 记录
     */
    @ResponseBody
    @RequestMapping("/load/{id}")
    public Advice load(@PathVariable("id") int id) {
        Advice advice = null;
        if (id > 0) {
            advice = this.adviceService.load(id);
        } 
        else {
            advice = new Advice(-1, -1, "", null,-1);
        }
        return advice;
    }

    /**
     * 精确判断是否存在记录
     * 
     * @param id 标识
     * @param request 查询参数
     * @return 记录条数
     */
    @RequestMapping("/exist/{id}")
    @ResponseBody
    public int exist(@PathVariable("id")int id, HttpServletRequest request) {
    	List<Advice> advices = this.adviceService.find(RequestUtil.asMap(request, false));
    	if (advices.size() == 1) {
    		return advices.get(0).getId() == id ? 0 : 1;
    	} else {
    		return advices.size();
    	}
    }

    /**
     * 插入记录
     *
     * @param advice Account实例
     * @return 被插入的记录标识
     */
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public int create(Advice advice) {
        return this.adviceService.insert(advice);
    }

    /**
     * POST 编辑
     *
     * @param id 标识
     * @param advice Account实例
     * @return int 被修改的记录条数
     * @throws ServletException
     */
    @ResponseBody
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public int edit(@PathVariable("id") int id, Advice advice) throws ServletException {
        return this.adviceService.update(advice);
    }

    /**
     * POST 删除多条
     *
     * @param ids 标识列表
     * @return 被删除的记录条数
     */
    @ResponseBody
    @RequestMapping(value = "/deletes", method = RequestMethod.POST)
    public int deletes(@RequestBody List<Integer> ids) {
        return this.adviceService.deletes(ids);
    }

    /**
     * 获取符合查询条件的记录，以idField值为key、nameField值为value作为一个Map
     *
     * @param idField key
     * @param nameField value
     * @param request 查询参数
     * @return 以idField值为key、nameField值为value的Map数组
     * @throws IllegalAccessException
     */
    @ResponseBody
    @RequestMapping("/list")
    public List<Map<String, Object>> list(@RequestParam(value="idField")String idField, @RequestParam(value="nameField")String nameField, HttpServletRequest request) throws IllegalAccessException {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        Map<String, Object> queryParams = RequestUtil.asMap(request, false);
        List<Advice> advices = adviceService.find(queryParams);
        for (int i = 0; i < advices.size(); i++) {
            Advice advice = advices.get(i);
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(idField, FieldUtils.readField(advice, idField, true));
            item.put(nameField, FieldUtils.readField(advice, nameField, true));
            items.add(item);
        }
        return items;
    }
}
