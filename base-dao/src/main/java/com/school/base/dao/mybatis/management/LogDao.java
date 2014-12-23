package com.school.base.dao.mybatis.management;

import org.springframework.stereotype.Repository;

import com.school.base.dao.face.management.ILogDao;
import com.school.base.dao.mybatis.Dao;
import com.school.base.domain.management.Log;

/**
 * T_LOG(日志) DAO实现
 *
 * @作者 work
 * @创建时间 2012-12-11 14:58:32
 */
@Repository
public class LogDao extends Dao<Log> implements ILogDao {
    
}