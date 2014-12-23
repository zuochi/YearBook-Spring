package com.school.base.dao.mybatis.interceptor.audit;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author work
 */
public class AuditConfigTable implements Serializable {

    private String name;
    private String domain;
    private boolean relationTable;
    private List<AuditConfigColumn> auditConfigColumns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<AuditConfigColumn> getAuditConfigColumns() {
        return auditConfigColumns;
    }

    public void setAuditConfigColumns(List<AuditConfigColumn> auditConfigColumns) {
        this.auditConfigColumns = auditConfigColumns;
    }

    public void setRelationTable(boolean relationTable) {
        this.relationTable = relationTable;
    }

    public boolean isRelationTable() {
        return this.relationTable;
    }
}
