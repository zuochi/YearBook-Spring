package com.school.base.dao.mybatis.interceptor;

import com.school.base.dao.face.DaoAccessException;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.result.DefaultResultHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author work
 */
public class InterceptorUtil {

    public static MappedStatement createMappedStatement(
            MappedStatement statement, String newSql,
            Object paramMap, List<ParameterMapping> parameterMappings, SqlCommandType commandType,
            ResultSetHandler resultSetHandler) {
        Configuration configuration = createConfiguration(statement, resultSetHandler);

        SqlSource source = statement.getSqlSource();
        BoundSqlWrapper boundSql = new BoundSqlWrapper(configuration, source.getBoundSql(paramMap), newSql);
        boundSql.setParameterMappings(parameterMappings);
        SqlSource sqlSource = new SqlSourceWrapper(boundSql);

        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statement.getId(), sqlSource, commandType);
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

    private static Configuration createConfiguration(MappedStatement statement, ResultSetHandler resultSetHandler) {
        ConfigurationWrapper configuration = new ConfigurationWrapper(statement.getConfiguration());
        configuration.setResultSetHandler(resultSetHandler);
        return configuration;
    }

    public static Invocation queryInvocation(Invocation invocation, MappedStatement newStatement, Object parameter, RowBounds rowBounds) throws NoSuchMethodException {
        Method query = CachingExecutor.class.getMethod("query", MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class);
        Object[] args = new Object[4];
        args[0] = newStatement;
        args[1] = parameter;
        args[2] = rowBounds;
        args[3] = new DefaultResultHandler();
        Invocation queryInvocation = new Invocation(invocation.getTarget(), query, args);
        return queryInvocation;
    }

    /**
     * SqlSource包装类，用于自定义BoundSql
     */
    private static class SqlSourceWrapper implements SqlSource {

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
     * Configuration包装类，用于自定义ResultSetHandler
     */
    private static class ConfigurationWrapper extends Configuration {

        private Configuration configuration;
        private ResultSetHandler resultSetHandler;

        public ConfigurationWrapper(Configuration configuration) {
            this.configuration = configuration;
            BeanUtils.copyProperties(this.configuration, this);
        }

        public void setResultSetHandler(ResultSetHandler resultSetHandler) {
            this.resultSetHandler = resultSetHandler;
        }

        @Override
        public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
            if (this.resultSetHandler != null) {
                return this.resultSetHandler;
            } else {
                return this.configuration.newResultSetHandler(executor, mappedStatement, rowBounds, parameterHandler, resultHandler, boundSql);
            }
        }
    }

    /**
     * BouldSql包装类，用于转换delete from为select id from
     */
    private static class BoundSqlWrapper extends BoundSql {

        private BoundSql boundSql;
        private String newSql;
        private List<ParameterMapping> parameterMappings;

        public BoundSqlWrapper(Configuration configuration, BoundSql boundSql, String newSql) {
            super(configuration, null, null, null);
            this.boundSql = boundSql;
            this.newSql = newSql;
        }

        @Override
        public String getSql() {
            return this.newSql != null ? this.newSql : this.boundSql.getSql();
        }

        public void setParameterMappings(List<ParameterMapping> parameterMappings) {
            this.parameterMappings = parameterMappings;
        }

        @Override
        public List<ParameterMapping> getParameterMappings() {
            return this.parameterMappings != null ? this.parameterMappings : boundSql.getParameterMappings();
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

    public static String getTable(String sql) {
        if (sql.trim().toUpperCase().startsWith("INSERT ")) {
            String upper = sql.toUpperCase();
            int start = upper.indexOf("INTO");
            int end = upper.indexOf("(");
            String table = upper.substring(start + "INFO".length(), end).replace(" ", "").trim();
            return table;
        } else if (sql.trim().toUpperCase().startsWith("UPDATE ")) {
            String upper = sql.toUpperCase();
            int end = upper.indexOf("SET");
            String table = upper.substring("UPDATE".length(), end).replace(" ", "").trim();
            return table;
        } else if (sql.trim().toUpperCase().startsWith("DELETE ")) {
            String upper = sql.toUpperCase();
            int start = upper.indexOf("FROM");
            int end = upper.indexOf("WHERE");
            String table = upper.substring(start + "FROM".length(), end).replace(" ", "").trim();
            return table;
        } else {
            throw new DaoAccessException(sql);
        }
    }
}
