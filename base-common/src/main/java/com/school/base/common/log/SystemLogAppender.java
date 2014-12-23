package com.school.base.common.log;

import com.school.base.common.security.UserInfo;
import com.school.base.domain.management.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 系统日志处理器
 *
 * @author work
 */
public class SystemLogAppender extends JDBCAppender {

    private PreparedStatement preparedStatement;
    private String insertSql;

    public SystemLogAppender() throws IOException {
        Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/jdbc.properties"));
        super.setDriver(properties.getProperty("jdbc.driver"));
        super.setURL(properties.getProperty("jdbc.url"));
        super.setUser(properties.getProperty("jdbc.username"));
        super.setPassword(properties.getProperty("jdbc.password"));
    }

    protected void execute(LoggingEvent event) throws SQLException {
        Log info = new Log();
        info.setCategory(2);
        info.setGrade(event.getLevel().toString());
        info.setLogger(event.getLogger().getName());
        info.setUrl(this.getUrl());
        info.setSource(this.getIpAddress());
        info.setSuccess(event.getThrowableInformation() == null ? 1 : 0);
        info.setThread(event.getThreadName());
        info.setWhat("");
        info.setWhen(new Date(event.getTimeStamp()));
        info.setWho(this.getLogUser());
        info.setParameter("");
        info.setResult("");
        info.setMessage(event.getMessage() != null ? StringUtils.left(event.getMessage().toString(), 1000) : "");
        info.setTrace(event.getThrowableInformation() != null ? this.getTrace(event.getThrowableInformation().getThrowable()) : "");

        String sql = this.insertSql;
        if (this.preparedStatement == null) {
            preparedStatement = super.getConnection().prepareStatement(sql);
        }

        int i = 1;
        PreparedStatement statement = this.preparedStatement;
        statement.setInt(i++, info.getCategory());
        statement.setString(i++, info.getThread());
        statement.setString(i++, info.getGrade());
        statement.setString(i++, info.getLogger());
        statement.setString(i++, info.getSource());
        statement.setString(i++, info.getMessage());
        statement.setObject(i++, info.getResult());
        statement.setObject(i++, info.getTrace());
        statement.setString(i++, info.getUrl());
        statement.setTimestamp(i++, new Timestamp(info.getWhen().getTime()));
        statement.setString(i++, info.getWho());
        statement.setString(i++, info.getWhat());
        statement.setInt(i++, info.getSuccess());
        statement.setObject(i++, info.getParameter());
        statement.executeUpdate();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void flushBuffer() {
        //Do the actual logging
        removes.ensureCapacity(buffer.size());
        for (Iterator i = buffer.iterator(); i.hasNext();) {
            LoggingEvent logEvent = (LoggingEvent) i.next();
            try {
                this.execute(logEvent);
            } catch (SQLException e) {
                errorHandler.error("Failed to excute sql", e,
                        ErrorCode.FLUSH_FAILURE);
            } finally {
                removes.add(logEvent);
            }
        }

        // remove from the buffer any events that were reported
        buffer.removeAll(removes);

        // clear the buffer of reported events
        removes.clear();
    }

    private String getIpAddress() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
            if (details instanceof UserInfo) {
                UserInfo userInfo = (UserInfo) details;
                return userInfo.getIpAddress();
            }
        }

        return "";
    }

    private String getLogUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getPrincipal().toString();
        }

        return "system";
    }

    public String getTrace(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String trace = writer.toString();
        return trace == null ? "" : trace;
    }

    private String getUrl() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null && attributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes request = (ServletRequestAttributes) attributes;
            HttpServletRequest servletRequest = request.getRequest();
            String queryString = servletRequest.getQueryString();
            return servletRequest.getRequestURI()
                    + (StringUtils.isNotBlank(queryString) ? "?" + queryString : "");
        }

        return "";
    }

    public String getInsertSql() {
        return insertSql;
    }

    public void setInsertSql(String insertSql) {
        this.insertSql = insertSql;
    }
}
