package com.school.base.dao.mybatis.interceptor;

import com.school.base.dao.mybatis.Dao;

/**
 * DAO修改操作拦截器
 *
 * @author work
 */
public interface DaoUpdateInterceptor<T> {

    void invoke(Dao dao, T obj);
}
