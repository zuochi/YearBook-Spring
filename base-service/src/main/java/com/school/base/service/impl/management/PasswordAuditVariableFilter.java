package com.school.base.service.impl.management;

import com.school.base.domain.management.AuditVariable;
import com.school.base.service.face.management.IAuditVariableFilter;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author work
 */
@Service
public class PasswordAuditVariableFilter implements IAuditVariableFilter {

    @Override
    public boolean hit(String table, String column) {
        return "PASSWORD".equals(column);
    }

    @Override
    public void process(AuditVariable variable) {
        if (StringUtils.isEmpty(variable.getOldValue())
                && !StringUtils.isEmpty(variable.getNewValue())) {
            variable.setNewValue("******");
        } else if (!StringUtils.isEmpty(variable.getOldValue())
                && StringUtils.isEmpty(variable.getNewValue())) {
            variable.setOldValue("******");
        } else if (!StringUtils.isEmpty(variable.getOldValue())
                && !StringUtils.isEmpty(variable.getNewValue())) {
            if (StringUtils.equals(variable.getOldValue(), variable.getNewValue())) {
                variable.setOldValue("******");
                variable.setNewValue("******");
            } else {
                variable.setOldValue("******");
                variable.setNewValue("********");
            }
        }
    }
}
