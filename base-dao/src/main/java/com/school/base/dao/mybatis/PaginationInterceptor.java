package com.school.base.dao.mybatis;

import java.util.List;
import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @deprecated @see
 * com.gz91pay.base.dao.mybatis.interceptor.PaginationInterceptor
 * @author work
 */
@Intercepts({
    @Signature(
            type = Executor.class,
            method = "query",
            args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
@Deprecated
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

            //开始转换
            String orgSql = oldBoundSql.getSql();
            String newSql = this.dialect.getLimitString(orgSql, bounds.getOffset(), bounds.getLimit());
            MappedStatement newStatement = this.createMappedStatement(statement, paramMap, newSql, SqlCommandType.SELECT);

            //分页转换后
            invocation.getArgs()[0] = newStatement;
            invocation.getArgs()[1] = paramMap;
            invocation.getArgs()[2] = new RowBounds(0, Integer.MAX_VALUE);

            return invocation.proceed();
        }
    }

    private MappedStatement createMappedStatement(final MappedStatement statement, Object paramMap, String newSql, SqlCommandType commandType) {
        BoundSql boundSql = new BoundSqlWrapper(statement.getConfiguration(), statement.getSqlSource().getBoundSql(paramMap), newSql);
        SqlSource sqlSource = new SqlSourceWrapper(boundSql);

        MappedStatement.Builder builder = new MappedStatement.Builder(statement.getConfiguration(), statement.getId(), sqlSource, commandType);
        builder.cache(statement.getCache());
        builder.databaseId(statement.getDatabaseId());
        builder.fetchSize(statement.getFetchSize());
        builder.flushCacheRequired(statement.isFlushCacheRequired());
        builder.keyGenerator(statement.getKeyGenerator());
        builder.parameterMap(statement.getParameterMap());
        builder.resource(statement.getResource());
        builder.resultMaps(statement.getResultMaps());
        builder.resultSetType(statement.getResultSetType());
        builder.statementType(statement.getStatementType());
        builder.timeout(statement.getTimeout());
        builder.useCache(statement.isUseCache());

        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * SqlSource包装类，用于自定义BoundSql
     */
    private class SqlSourceWrapper implements SqlSource {

        private BoundSql boundSql;

        public SqlSourceWrapper(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return this.boundSql;
        }
    }

    /**
     * BouldSql包装类，用于转换sql
     */
    private class BoundSqlWrapper extends BoundSql {

        private BoundSql boundSql;
        private String newSql;

        public BoundSqlWrapper(Configuration configuration, BoundSql boundSql, String newSql) {
            super(configuration, null, null, null);
            this.boundSql = boundSql;
            this.newSql = newSql;
        }

        @Override
        public String getSql() {
            return this.newSql != null ? this.newSql : this.boundSql.getSql();
        }

        @Override
        public List<ParameterMapping> getParameterMappings() {
            return boundSql.getParameterMappings();
        }

        @Override
        public Object getParameterObject() {
            return boundSql.getParameterObject();
        }

        @Override
        public boolean hasAdditionalParameter(String name) {
            return boundSql.hasAdditionalParameter(name);
        }

        @Override
        public void setAdditionalParameter(String name, Object value) {
            boundSql.setAdditionalParameter(name, value);
        }

        @Override
        public Object getAdditionalParameter(String name) {
            return boundSql.getAdditionalParameter(name);
        }
    }
}
