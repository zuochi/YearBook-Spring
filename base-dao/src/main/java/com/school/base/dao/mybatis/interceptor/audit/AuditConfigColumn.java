package com.school.base.dao.mybatis.interceptor.audit;

import java.io.Serializable;

/**
 *
 * @author work
 */
public class AuditConfigColumn implements Serializable {

    private String name;
    private String field;
    private String description;
    private String foreignTable;
    private String foreignKeyColumn;
    private String foreignNameColumn;
    private String foreignSql;

    public void setForeignSql(String foreignSql) {
        this.foreignSql = foreignSql;
    }

    public String getForeignSql() {
        return foreignSql;
    }

    public String getForeignKeyColumn() {
        return foreignKeyColumn == null ? "ID" : foreignKeyColumn;
    }

    public void setForeignKeyColumn(String foreignKeyColumn) {
        this.foreignKeyColumn = foreignKeyColumn;
    }

    public String getForeignNameColumn() {
        return foreignNameColumn == null ? "NAME" : foreignNameColumn;
    }

    public void setForeignNameColumn(String foreignNameColumn) {
        this.foreignNameColumn = foreignNameColumn;
    }

    public String getForeignTable() {
        return foreignTable;
    }

    public void setForeignTable(String foreignTable) {
        this.foreignTable = foreignTable;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isForeignKey() {
        return this.foreignTable != null;
    }
}
