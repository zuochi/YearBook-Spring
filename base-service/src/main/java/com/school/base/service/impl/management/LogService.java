package com.school.base.service.impl.management;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.base.dao.face.management.ILogDao;
import com.school.base.domain.management.Log;
import com.school.base.service.face.management.ILogService;

/**
 * T_LOG(日志) 服务实现
 *
 * @作者 work
 * @创建时间 2012-12-11 14:58:32
 */
@Service
public class LogService implements ILogService {
   
    @Autowired
    private ILogDao logDao;
    
    @Override
    public int count(Map<String, Object> queryParams) {
        return this.logDao.count(queryParams);
    }

    @Override
    public List<Log> page(int start, int limit, Map<String, Object> queryParams) {
        return this.logDao.page(start, limit, queryParams);
    }

    @Override
    public List<Log> find(Map<String, Object> queryParams) {
        return this.logDao.find(queryParams);
    }

    @Override
    public int generatePosition(Map<String, Object> queryParams) {
        return this.logDao.generatePosition(queryParams);
    }

    @Override
    public Log load(int id) {
        return this.logDao.load(id);
    }

    @Override
    @Transactional
    public int insert(Log log) {
        this.logDao.insert(log);
        return log.getId();
    }

    @Override
    @Transactional
    public int update(Log log) {
        return this.logDao.update(log);
    }

	@Override
    @Transactional
    public int deletes(int[] ids) {
        return this.logDao.deletes(ids);
    }
}
