package com.school.base.domain.management;

import java.io.Serializable;
import java.util.Date;

/**
 * T_LOG(日志) 表的映射
 *
 * @作者 work
 * @创建时间 2012-12-11 14:58:32
 */
public class Log implements Serializable {

  private static final long serialVersionUID = 1L;
  //标识
  private Integer id;
  //类型
  private Integer category;
  //时间
  private Date when;
  //来源IP
  private String source = "";
  //用户
  private String who = "";
  //事件
  private String what = "";
  //访问地址
  private String url = "";
  //参数
  private String parameter = "";
  //返回结果
  private String result = "";
  //状态
  private Integer success;
  //信息
  private String message = "";
  //线程
  private String thread = "";
  //记录器
  private String logger = "";
  //等级
  private String grade = "";
  //堆栈信息
  private String trace = "";

  public Log() {
  }

  public Log(Integer id, Integer category, Date when, String source, String who, String what, String url, String parameter, String result, Integer success, String message, String thread, String logger, String grade, String trace) {
    this.id = id;
    this.category = category;
    this.when = when;
    this.source = source;
    this.who = who;
    this.what = what;
    this.url = url;
    this.parameter = parameter;
    this.result = result;
    this.success = success;
    this.message = message;
    this.thread = thread;
    this.logger = logger;
    this.grade = grade;
    this.trace = trace;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return this.id;
  }

  public void setCategory(Integer category) {
    this.category = category;
  }

  public Integer getCategory() {
    return this.category;
  }

  public void setWhen(Date when) {
    this.when = when;
  }

  public Date getWhen() {
    return this.when;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getSource() {
    return this.source;
  }

  public void setWho(String who) {
    this.who = who;
  }

  public String getWho() {
    return this.who;
  }

  public void setWhat(String what) {
    this.what = what;
  }

  public String getWhat() {
    return this.what;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return this.url;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public String getParameter() {
    return this.parameter;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getResult() {
    return this.result;
  }

  public void setSuccess(Integer success) {
    this.success = success;
  }

  public Integer getSuccess() {
    return this.success;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }

  public void setThread(String thread) {
    this.thread = thread;
  }

  public String getThread() {
    return this.thread;
  }

  public void setLogger(String logger) {
    this.logger = logger;
  }

  public String getLogger() {
    return this.logger;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }

  public String getGrade() {
    return this.grade;
  }

  public void setTrace(String trace) {
    this.trace = trace;
  }

  public String getTrace() {
    return this.trace;
  }
}
