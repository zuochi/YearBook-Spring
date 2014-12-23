package com.school.base.dao.mybatis.management;

import com.school.base.dao.face.management.IAuditVariableDao;
import com.school.base.dao.mybatis.Dao;
import com.school.base.domain.management.AuditVariable;

import org.springframework.stereotype.Repository;

/**
 *
 * @author work
 */
@Repository
public class AuditVariableDao extends Dao<AuditVariable> implements IAuditVariableDao {
}
