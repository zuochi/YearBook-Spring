package com.school.yearbook.service.impl.management;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.base.common.util.StringUtil;
import com.school.yearbook.dao.face.management.IAdivceDao;
import com.school.yearbook.domain.management.Advice;
import com.school.yearbook.service.face.management.IAdviceService;

/**
 * advice(建议表)服务实现
 * @author Gilbert
 *
 * 2014年12月22日
 */
@Service
public class AdviceService implements IAdviceService {
   
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
   
    @Autowired
    private IAdivceDao adivceDao;
    
    @Override
    public int count(Map<String, Object> queryParams) {
        return this.adivceDao.count(queryParams);
    }

    @Override
    public List<Advice> page(int start, int limit, Map<String, Object> queryParams) {
    	String sort = ObjectUtils.toString(queryParams.get("sort"));
    	queryParams.put("sort", StringUtil.cammelToUnderlinePatterm(sort));
        return this.adivceDao.page(start, limit, queryParams);
    }

    @Override
    public List<Advice> find(Map<String, Object> queryParams) {
        return this.adivceDao.find(queryParams);
    }

    @Override
    public int generatePosition(Map<String, Object> queryParams) {
        return this.adivceDao.generatePosition(queryParams);
    }

    @Override
    public Advice load(int id) {
        return this.adivceDao.load(id);
    }

    @Override
    @Transactional
    public int insert(Advice advice) {
        this.adivceDao.insert(advice);
        return advice.getId();
    }

    @Override
    @Transactional
    public int update(Advice advice) {
        return this.adivceDao.update(advice);
    }

	@Override
    @Transactional
    public int deletes(List<Integer> ids) {
        return this.adivceDao.deletes(ids);
    }
}
