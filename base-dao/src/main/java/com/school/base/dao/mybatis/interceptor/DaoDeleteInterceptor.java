package com.school.base.dao.mybatis.interceptor;

import com.school.base.dao.mybatis.Dao;

import java.util.Map;

/**
 * DAO删除操作拦截器
 *
 * @author work
 */
public interface DaoDeleteInterceptor<T> {
    void invoke(Dao dao, Map<String, Object> queryParams);
}
