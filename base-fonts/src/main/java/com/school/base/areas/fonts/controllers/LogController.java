package com.school.base.areas.fonts.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.school.base.common.easyui.DataGridModel;
import com.school.base.common.mvc.MutilCustomDateEditor;
import com.school.base.common.web.RequestUtil;
import com.school.base.domain.management.Log;
import com.school.base.service.face.management.ILogService;

/**
 * T_LOG(日志) 控制器
 *
 * @作者 work
 * @创建时间 2012-12-11 14:58:32
 */
@Controller("management.logController")
@RequestMapping("/management/log")
public class LogController {

  @Autowired
  private ILogService logService;

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
  public String index(int category) {
    return category == 1
            ? "/areas/management/views/log/user"
            : "/areas/management/views/log/system";
  }

  /**
   * GET 创建
   *
   * @return 视图路径
   */
  @RequestMapping("/create")
  public String create() {
    return "/areas/management/views/log/create";
  }

  /**
   * GET 编辑
   *
   * @param id 标识
   * @return 视图路径
   */
  @RequestMapping("/edit/{id}")
  public String edit(@PathVariable("id") int id) {
    return "/areas/management/views/log/edit";
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
    return this.logService.count(RequestUtil.asMap(request));
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
  public DataGridModel<Log> page(int page, int rows, int total, HttpServletRequest request) {
    int start = (page - 1) * rows;
    int limit = rows;
    List<Log> list = this.logService.page(start, limit, RequestUtil.asMap(request));
    return new DataGridModel<Log>(list, total);
  }

  /**
   * 根据标识获取记录
   *
   * @param id 标识
   * @return 记录
   */
  @ResponseBody
  @RequestMapping("/load/{id}")
  public Log load(@PathVariable("id") int id) {
    Log log = null;
    if (id > 0) {
      log = this.logService.load(id);
    } else {
      log = new Log(
              -1,
              -1,
              new Date(),
              "",
              "",
              "",
              "",
              "",
              "",
              -1,
              "",
              "",
              "",
              "",
              "");
    }
    return log;
  }

  /**
   * 精确判断是否存在记录
   *
   * @param request 查询参数
   * @return 记录条数
   */
  @RequestMapping("/exist")
  @ResponseBody
  public int exist(HttpServletRequest request) {
    return this.logService.find(RequestUtil.asMap(request)).size();
  }

  /**
   * 插入记录
   *
   * @param log Log实例
   * @return 被插入的记录标识
   */
  @ResponseBody
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public int create(Log log) {
    return this.logService.insert(log);
  }

  /**
   * POST 编辑
   *
   * @param id 标识
   * @param log Log实例
   * @return int 被修改的记录条数
   * @throws ServletException
   */
  @ResponseBody
  @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
  public int edit(@PathVariable("id") int id, Log log) throws ServletException {
    return this.logService.update(log);
  }

  /**
   * POST 删除多条
   *
   * @param ids 标识列表
   * @return 被删除的记录条数
   */
  @ResponseBody
  @RequestMapping(value = "/deletes", method = RequestMethod.POST)
  public int deletes(int[] ids) {
    return this.logService.deletes(ids);
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
  @RequestMapping("/name")
  public List<Map<String, Object>> name(String idField, String nameField, HttpServletRequest request) throws IllegalAccessException {
    List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
    Map<String, Object> queryParams = RequestUtil.asMap(request);
    List<Log> logs = logService.find(queryParams);
    for (int i = 0; i < logs.size(); i++) {
      Log log = logs.get(i);
      Map<String, Object> item = new HashMap<String, Object>();
      item.put(idField, FieldUtils.readField(log, idField, true));
      item.put(nameField, FieldUtils.readField(log, nameField, true));
      items.add(item);
    }
    return items;
  }
}
