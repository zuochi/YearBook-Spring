package com.school.base.dao.mybatis.interceptor;

import com.school.base.dao.mybatis.Dao;

/**
 * DAO插入操作拦截器
 *
 * @author work
 */
public interface DaoInsertInterceptor<T> {

    void invoke(Dao dao, T obj);
}
