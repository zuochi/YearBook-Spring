package com.school.base.domain.management;

import com.school.base.domain.Constants;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * T_AUDIT( ) 表的映射
 *
 * @作者 work
 * @创建时间 2012-12-11 14:58:32
 */
public class Audit implements Serializable {

    private static final long serialVersionUID = 1L;
    //标识
    private Integer id = -1;
    //表名
    @NotNull
    private String tbl = "";
    //键值
    @NotNull
    @Min(0)
    private Integer key = -1;
    //操作
    @NotNull
    private String operation = "";
    //Who
    @NotNull
    private String who = "";
    //When
    @NotNull
    private Date when = new Date();
    //
    private transient List<AuditVariable> variables;

    public Audit() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public void setTbl(String tbl) {
        this.tbl = tbl;
    }

    public String getTbl() {
        return this.tbl;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return this.key;
    }

    /**
     *
     * @param operation
     * @deprecated setCreateOperation setModifyOperation setDeleteOperation
     * @see setCreateOperation()
     * @see setModifyOperation()
     * @see setDeleteOperation()
     */
    @Deprecated
    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getWho() {
        return this.who;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public Date getWhen() {
        return this.when;
    }

    public void setCreateOperation() {
        this.operation = Constants.AUDIT_OPERATION_ADD;
    }

    public void setModifyOperation() {
        this.operation = Constants.AUDIT_OPERATION_MODIFY;
    }

    public void setDeleteOperation() {
        this.operation = Constants.AUDIT_OPERATION_DELETE;
    }

    public boolean isCreateOperation() {
        return Constants.AUDIT_OPERATION_ADD.equals(this.operation);
    }

    public boolean isDeleteOperation() {
        return Constants.AUDIT_OPERATION_DELETE.equals(this.operation);
    }

    public boolean isModifyOperation() {
        return Constants.AUDIT_OPERATION_MODIFY.equals(this.operation);
    }

    public void setVariables(List<AuditVariable> variables) {
        this.variables = variables;
    }

    public List<AuditVariable> getVariables() {
        return variables;
    }
}
