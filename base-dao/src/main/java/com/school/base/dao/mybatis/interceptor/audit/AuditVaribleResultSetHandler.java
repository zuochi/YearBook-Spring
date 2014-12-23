package com.school.base.dao.mybatis.interceptor.audit;

import com.school.base.domain.management.AuditVariable;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author work
 */
@Service
public class AuditVaribleResultSetHandler implements ResultSetHandler {

    private String tableName;
    @Autowired
    private AuditConfig auditConfig;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public List<AuditVariable> handleResultSets(Statement stmt) throws SQLException {
        if (!this.auditConfig.variableEnable(this.tableName)) {
            return Collections.EMPTY_LIST;
        }
        //
        List<AuditVariable> list = new ArrayList();
        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            List<AuditConfigColumn> columns = this.auditConfig.get(this.tableName);
            for (AuditConfigColumn column : columns) {
                AuditVariable variable = new AuditVariable();
                variable.setFieldName(column.getName());
                variable.setFieldDesc(column.getDescription());
                String obj = rs.getString(column.getName());
                variable.setOldValue(obj != null ? obj : "");
                variable.setNewValue(variable.getOldValue());
                list.add(variable);
            }
        }
        return list;
    }

    @Override
    public void handleOutputParameters(CallableStatement cs) throws SQLException {
        //
    }
}
