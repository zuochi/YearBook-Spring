package com.school.base.dao.mybatis.interceptor;

import com.school.base.dao.mybatis.*;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author work
 */
@Intercepts({
    @Signature(
            type = Executor.class,
            method = "query",
            args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PaginationInterceptor implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Dialect dialect;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //分页转换前
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
        Object paramMap = invocation.getArgs()[1];
        RowBounds bounds = (RowBounds) invocation.getArgs()[2];
        if (bounds.getLimit() == Integer.MAX_VALUE) {
            //没有分页信息，直接返回查询
            return invocation.proceed();
        } else {
            //转换
            BoundSql oldBoundSql = statement.getBoundSql(paramMap);
            String databaseId = statement.getConfiguration().getDatabaseId();
            if ("Oracle".equals(databaseId)) {
                this.dialect = new OracleDialect();
            } else {
                this.logger.warn("没有发现" + databaseId + "数据库的物理分页处理器，直接使用mybatis的逻辑分页");
                return invocation.proceed();
            }

            //分页
            String orgSql = oldBoundSql.getSql();
            String newSql = this.dialect.getLimitString(orgSql, bounds.getOffset(), bounds.getLimit());
            MappedStatement newStatement = InterceptorUtil.createMappedStatement(
                    statement, newSql, paramMap, null, SqlCommandType.SELECT, null);
            Invocation newInvocation = InterceptorUtil.queryInvocation(
                    invocation, newStatement, paramMap, new RowBounds(0, Integer.MAX_VALUE));
            return newInvocation.proceed();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
