/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.school.base.dao.mybatis.interceptor;

import com.school.base.dao.face.DaoAccessException;

import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import static org.apache.ibatis.mapping.SqlCommandType.INSERT;
import static org.apache.ibatis.mapping.SqlCommandType.UPDATE;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

/**
 *
 * @author work
 */
@Intercepts({
    @Signature(
            type = Executor.class,
            method = "update",
            args = {MappedStatement.class, Object.class})})
public class ValidatorInteceptor implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private Validator validator;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //获取参数
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        SqlCommandType commandType = statement.getSqlCommandType();
        //
        //
        switch (commandType) {
            case INSERT:
            case UPDATE:
                this.validate(parameter);
                return invocation.proceed();
            default:
                return invocation.proceed();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private void validate(Object domain) {
        Errors errors = new BeanPropertyBindingResult(domain, "errors");
        this.validator.validate(domain, errors);
        List<ObjectError> objectErrors = errors.getAllErrors();
        boolean hasError = objectErrors.size() > 0;
        if (hasError) {
            StringBuilder builder = new StringBuilder();
            builder.append("\n");
            builder.append("        --------------------------------------------------------------------------------");
            builder.append("\n");
            builder.append("        验证失败：").append(domain.getClass().getName()).append("\n");
            for (ObjectError objectError : objectErrors) {
                this.logger.error(objectError.toString());
                //
                if (objectError instanceof FieldError) {
                    FieldError fieldError = (FieldError) objectError;
                    builder.append("        字段: ").append(fieldError.getField()).append("; ")
                            .append("值: ").append(fieldError.getRejectedValue()).append("; ")
                            .append("信息: ").append(fieldError.getDefaultMessage()).append("\n");
                } else {
                    builder.append(objectError.toString()).append("\n");
                }
            }
            builder.append("        --------------------------------------------------------------------------------");
            builder.append("\n");

            throw new DaoAccessException(builder.toString());
        }
    }
}