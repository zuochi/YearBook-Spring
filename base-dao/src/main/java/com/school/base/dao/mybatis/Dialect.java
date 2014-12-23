package com.school.base.dao.mybatis;

/**
 * @author work
 */
public interface Dialect {

    String getLimitString(String sql, int offset, int limit);
}
