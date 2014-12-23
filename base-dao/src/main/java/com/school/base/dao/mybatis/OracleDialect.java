package com.school.base.dao.mybatis;

import org.springframework.stereotype.Service;

/**
 * @author work
 */
@Service
public class OracleDialect implements Dialect {

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        sql = sql.trim();
        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        pagingSelect.append(sql);
        pagingSelect.append(" ) row_ ) where rownum_ > ").append(offset).append(" and rownum_ <= ").append(offset + limit);
        return pagingSelect.toString();
    }
}
