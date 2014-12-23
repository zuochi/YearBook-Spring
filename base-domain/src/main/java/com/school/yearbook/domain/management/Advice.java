package com.school.yearbook.domain.management;

import java.io.Serializable;
import java.util.Date;


public class Advice implements Serializable {


	private static final long serialVersionUID = 1L;
	//标识
	private Integer id;
	//建议用户ID
	private Integer userId;
	//建议内容
	private String context;
	//建议日期
	private Date signupDate;
	//是否删除
	private Integer isDelete;
	
	public Advice() {
	}
	public Advice(Integer id, Integer userId, String context, Date signupDate,
			Integer isDelete) {
		super();
		this.id = id;
		this.userId = userId;
		this.context = context;
		this.signupDate = signupDate;
		this.isDelete = isDelete;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public Date getSignupDate() {
		return signupDate;
	}
	public void setSignupDate(Date signupDate) {
		this.signupDate = signupDate;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	
	
}