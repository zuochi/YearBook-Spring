package com.school.base.dao.mybatis.interceptor.audit;

import com.school.base.dao.mybatis.interceptor.InterceptorUtil;
import com.school.base.domain.management.AuditVariable;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 拦截器
 *
 * @author work
 */
@Intercepts({
    @Signature(
            type = Executor.class,
            method = "update",
            args = {MappedStatement.class, Object.class})})
public class AuditInterceptor implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AuditInterceptorService auditInterceptorService;
    @Autowired
    private AuditConfig auditConfig;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            //获取参数
            MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = statement.getBoundSql(parameter);
            String sql = boundSql.getSql();
            SqlCommandType commandType = statement.getSqlCommandType();
            //
            this.logger.debug("sql: " + sql);
            this.logger.debug("commandType: " + commandType);
            //
            switch (commandType) {
                case INSERT:
                    if (this.auditConfig.domainEnable(parameter)) {
                        //执行SQL操作
                        Object obj = invocation.proceed();
                        //构造审计信息
                        this.auditInterceptorService.insert(invocation, statement, sql, parameter);
                        return obj;
                    }
                    return invocation.proceed();
                case UPDATE:
                    if (this.auditConfig.domainEnable(parameter)) {
                        //构造审计信息
                        List<AuditVariable> befores = this.auditInterceptorService.selectVariable(
                                invocation, statement, sql, parameter, InterceptorUtil.getTable(sql),
                                (Integer) FieldUtils.readDeclaredField(parameter, "id", true));
                        //执行SQL操作
                        Object obj = invocation.proceed();
                        //构造审计信息
                        this.auditInterceptorService.update(invocation, statement, sql, parameter, befores);
                        return obj;
                    }
                    return invocation.proceed();
                case DELETE:
                    //构造审计信息
                    if (this.auditConfig.sqlEnable(sql)) {
                        this.auditInterceptorService.delete(invocation, statement, sql, parameter);
                    }
                    //执行SQL操作
                    return invocation.proceed();
                default:
                    return invocation.proceed();
            }

        } catch (Throwable e) {
            this.logger.error(e.getMessage(), e);
            throw e;
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
