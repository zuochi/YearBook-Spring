package com.school.base.common.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.school.base.common.security.IContextService;
import com.school.base.common.security.UserInfo;
import com.school.base.common.util.StringUtil;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户日志拦截器，包括操作拦截器和错误拦截器
 *
 * @author work
 */
@Service
public class LogInterceptor implements ThrowsAdvice, AfterReturningAdvice, MethodBeforeAdvice {

    @Autowired
    private IContextService contextService;
    private ILogHandler actionLogHandler;
    private Properties properties;
    private final ObjectMapper mapper;

    public LogInterceptor() {
        this.mapper = new ObjectMapper();
        this.mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void before(Method method, Object[] args, Object target)
            throws Throwable {
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target)
            throws Throwable {
        if (this.actionLogHandler != null) {
            com.school.base.domain.management.Log info = this.buildLogInfo(returnValue, method, args, target);
            this.actionLogHandler.handle(info, null);
        }
    }

    public void afterThrowing(Method method, Object[] args, Object target, Exception e) throws IOException {
        if (this.actionLogHandler != null) {
            com.school.base.domain.management.Log info = this.buildLogInfo("", method, args, target);
            if (info != null) {
                info.setSuccess(0);
                info.setMessage(StringUtils.left(e.getMessage(), 1000));
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                info.setTrace(writer.toString());
            }
            this.actionLogHandler.handle(info, e);
        }
    }

    public void setActionLogHandler(ILogHandler actionLogHandler) {
        this.actionLogHandler = actionLogHandler;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private com.school.base.domain.management.Log buildLogInfo(Object returnValue, Method method, Object[] args, Object target) throws IOException {
        if (method.isAnnotationPresent(Log.class)) {
            Log log = method.getAnnotation(Log.class);

            com.school.base.domain.management.Log info = new com.school.base.domain.management.Log();
            info.setCategory(1);
            info.setGrade("");
            info.setLogger(this.getClass().getName());
            info.setUrl(this.getUrl(target, method));
            info.setSource(this.getIpAddress());
            info.setSuccess(1);
            info.setThread(Thread.currentThread().getName());
            info.setWhat(this.getWhat(log, target, method, args));
            info.setWhen(new Date());
            info.setWho(this.contextService.getUsername());
            //防止不可序列化的参数
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (!(arg instanceof Serializable)) {
                        args[i] = arg.toString();
                    }
                }
            }
            info.setParameter(args != null && args.length > 0
                    ? StringUtil.convertIllegalStrings(this.mapper.writeValueAsString(args)) : "");
            info.setResult(returnValue != null
                    ? StringUtil.convertIllegalStrings(this.mapper.writeValueAsString(returnValue)) : "");
            info.setMessage(this.getMessage(log, args));
            info.setTrace("");
            return info;
        } else {
            return null;
        }
    }

    private String getWhat(Log log, Object target, Method method, Object[] args) {
        String name = log.name();
        String nameKey = log.nameKey();
        if (this.properties != null && nameKey != null && nameKey.trim().length() > 0) {
            String value = this.properties.getProperty(nameKey);
            return value != null ? value : nameKey;
        } else {
            if (name != null && name.trim().length() > 0) {
                int size = args != null ? args.length : 0;
                for (int i = 0; i < size; i++) {
                    name = name.replace("{" + i + "}", args[i].toString());
                }
                return name;
            } else {
                return target.getClass().getName() + "@" + method.getName();
            }
        }
    }

    private String getMessage(Log log, Object[] args) {
        String message = log.message();
        String messageKey = log.messageKey();
        if (this.properties != null && messageKey != null && messageKey.trim().length() > 0) {
            String value = this.properties.getProperty(messageKey);
            return value != null ? value : messageKey;
        } else {
            if (message != null && message.trim().length() > 0) {
                int size = args != null ? args.length : 0;
                for (int i = 0; i < size; i++) {
                    message = message.replace("{" + i + "}", args[i] != null ? args[i].toString() : "null");
                }
                return message;
            } else {
                return "";
            }
        }
    }

    private String getIpAddress() {
        UserInfo userInfo = this.contextService.getUserInfo();
        return userInfo.getIpAddress();
    }

    private String getUrl(Object target, Method method) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null && attributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes request = (ServletRequestAttributes) attributes;
            HttpServletRequest servletRequest = request.getRequest();
            String queryString = servletRequest.getQueryString();
            return servletRequest.getRequestURI()
                    + (StringUtils.isNotBlank(queryString) ? "?" + queryString : "");
        } else {
            StringBuilder builder = new StringBuilder();
            if (target.getClass().isAnnotationPresent(RequestMapping.class)) {
                RequestMapping mapping = target.getClass().getAnnotation(RequestMapping.class);
                builder.append(mapping.value()[0]);
            }
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                builder.append(mapping.value()[0]);
            }
            return builder.length() > 0 ? builder.toString() : target.getClass().getName() + "@" + method.getName();
        }
    }
}
