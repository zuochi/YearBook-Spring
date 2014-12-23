package com.school.base.dao.mybatis.management;

import org.springframework.stereotype.Repository;

import com.school.base.dao.face.management.IAuditDao;
import com.school.base.dao.mybatis.Dao;
import com.school.base.domain.management.Audit;

/**
 * T_AUDIT( ) DAO实现
 *
 * @作者 work
 * @创建时间 2012-12-11 14:58:32
 */
@Repository
public class AuditDao extends Dao<Audit> implements IAuditDao {
}