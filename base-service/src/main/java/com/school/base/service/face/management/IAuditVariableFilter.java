package com.school.base.service.face.management;

import com.school.base.domain.management.AuditVariable;

/**
 *
 * @author work
 */
public interface IAuditVariableFilter {

    boolean hit(String table, String column);

    void process(AuditVariable variable);
}
