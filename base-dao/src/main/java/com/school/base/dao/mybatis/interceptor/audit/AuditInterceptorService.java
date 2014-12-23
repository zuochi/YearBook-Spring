package com.school.base.dao.mybatis.interceptor.audit;

import com.school.base.domain.management.AuditVariable;

import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;

/**
 *
 * @author work
 */
public interface AuditInterceptorService {

    List<AuditVariable> selectVariable(Invocation invocation, MappedStatement statement, String sql, Object parameter, String table, Integer id) throws Throwable;

    void insert(Invocation invocation, MappedStatement statement, String sql, Object parameter) throws Throwable;

    void update(Invocation invocation, MappedStatement statement, String sql, Object parameter, List<AuditVariable> befores) throws Throwable;

    void delete(Invocation invocation, MappedStatement statement, String sql, Object parameter) throws Throwable;
}
