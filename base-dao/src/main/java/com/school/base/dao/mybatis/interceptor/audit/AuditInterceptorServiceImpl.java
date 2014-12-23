package com.school.base.dao.mybatis.interceptor.audit;

import com.school.base.dao.face.management.IAuditDao;
import com.school.base.dao.face.management.IAuditVariableDao;
import com.school.base.dao.mybatis.interceptor.InterceptorUtil;
import com.school.base.domain.Constants;
import com.school.base.domain.management.Audit;
import com.school.base.domain.management.AuditVariable;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author work
 */
@Service
public class AuditInterceptorServiceImpl implements AuditInterceptorService {

    @Autowired
    private AuditConfig auditConfig;
    @Autowired
    private IAuditDao auditDao;
    @Autowired
    private IAuditVariableDao auditVariableDao;
    @Autowired
    private AuditVaribleResultSetHandler auditVaribleResultSetHandler;

    @Override
    public void insert(Invocation invocation, MappedStatement statement, String sql, Object parameter) throws IllegalAccessException, Throwable {
        //
        Audit audit = this.createAudit(sql);
        audit.setKey((Integer) FieldUtils.readDeclaredField(parameter, "id", true));
        this.auditDao.insert(audit);
        //
        List<AuditVariable> list = this.selectVariable(invocation, statement, sql, parameter, audit.getTbl(), audit.getKey());
        for (AuditVariable variable : list) {
            variable.setAuditId(audit.getId());
            variable.setOldValue("");
            this.auditVariableDao.insert(variable);
        }
    }

    @Override
    public void update(Invocation invocation, MappedStatement statement, String sql, Object parameter, List<AuditVariable> befores) throws IllegalAccessException, Throwable {
        //
        Audit audit = this.createAudit(sql);
        audit.setKey((Integer) FieldUtils.readDeclaredField(parameter, "id", true));
        this.auditDao.insert(audit);
        //
        List<AuditVariable> list = this.selectVariable(invocation, statement, sql, parameter, audit.getTbl(), audit.getKey());
        for (AuditVariable variable : list) {
            for (AuditVariable before : befores) {
                if (variable.getFieldName().equals(before.getFieldName())) {
                    variable.setOldValue(before.getOldValue());
                    break;
                }
            }
            variable.setAuditId(audit.getId());
            this.auditVariableDao.insert(variable);
        }
    }

    @Override
    public void delete(Invocation invocation, MappedStatement statement, String sql, Object parameter) throws Throwable {
        String newSql = this.getQuerySql(sql);
        MappedStatement newStatement = InterceptorUtil.createMappedStatement(
                statement, newSql, parameter, null, SqlCommandType.SELECT, new ListIntegerResultSetHandler());
        Invocation queryInvocation = InterceptorUtil.queryInvocation(invocation,
                newStatement, parameter, new RowBounds(0, Integer.MAX_VALUE));
        List<Integer> entities = (List) queryInvocation.proceed();
        for (Integer id : entities) {
            Audit audit = this.createAudit(sql);
            audit.setKey(id);
            this.auditDao.insert(audit);

            List<AuditVariable> list = this.selectVariable(invocation, statement, sql, parameter, audit.getTbl(), audit.getKey());
            for (AuditVariable variable : list) {
                variable.setAuditId(audit.getId());
                variable.setNewValue("");
                this.auditVariableDao.insert(variable);
            }
        }
    }

    private Audit createAudit(String sql) {
        Audit audit = new Audit();
        audit.setWhen(new Date());
        this.setOperation(audit, sql);
        audit.setTbl(InterceptorUtil.getTable(sql));
        //获取当前登录用户信息
        audit.setWho(this.getUsername());
        return audit;
    }

    private void setOperation(Audit audit, String sql) {
        if (sql.toUpperCase().startsWith("INSERT ")) {
            audit.setOperation(Constants.AUDIT_OPERATION_ADD);
        } else if (sql.toUpperCase().startsWith("UPDATE ")) {
            audit.setOperation(Constants.AUDIT_OPERATION_MODIFY);
        } else if (sql.toUpperCase().startsWith("DELETE ")) {
            audit.setOperation(Constants.AUDIT_OPERATION_DELETE);
        } else {
            //Do nothing
        }
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getPrincipal().toString();
        } else {
            return "system";
        }
    }

    private String getQuerySql(String sql) {
        String upper = sql.toUpperCase();
        int index = upper.indexOf("FROM");
        String query = upper.substring(index);
        return "SELECT ID " + query;
    }

    @Override
    public List<AuditVariable> selectVariable(Invocation invocation, MappedStatement statement, String sql, Object parameter, String table, Integer id) throws Throwable {
        String newSql = "SELECT * FROM " + table + " WHERE ID=" + id;
        this.auditVaribleResultSetHandler.setTableName(table);
        MappedStatement newStatement = InterceptorUtil.createMappedStatement(
                statement, newSql, new HashMap(), new ArrayList(), SqlCommandType.SELECT,
                this.auditVaribleResultSetHandler);
        Invocation newInvocation = InterceptorUtil.queryInvocation(
                invocation, newStatement, new HashMap(), new RowBounds(0, Integer.MAX_VALUE));

        List<AuditVariable> list = (List) newInvocation.proceed();
        return list;
    }
    
    private class ListIntegerResultSetHandler implements ResultSetHandler {

        @Override
        public List<Integer> handleResultSets(Statement stmt) throws SQLException {
            ResultSet rs = stmt.getResultSet();
            List<Integer> list = new ArrayList<Integer>();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
            return list;
        }

        @Override
        public void handleOutputParameters(CallableStatement cs) throws SQLException {
            //
        }
    }
}
