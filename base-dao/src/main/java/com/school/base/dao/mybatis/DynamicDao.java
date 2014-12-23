package com.school.base.dao.mybatis;

import com.school.base.dao.face.IDynamicDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author work
 */
@Repository
public class DynamicDao extends SqlSessionDaoSupport implements IDynamicDao {

    @Override
    public HashMap selectOne(String dynamicSql) {
        return (HashMap) super.getSqlSession().selectOne(IDynamicDao.class.getName() + ".selectOne", dynamicSql);
    }

    @Override
    public List<Map> selectList(String dynamicSql) {
        return super.getSqlSession().selectList(IDynamicDao.class.getName() + ".selectList", dynamicSql);
    }

    @Override
    public void execute(String dynamicSql) {
        super.getSqlSession().selectOne(IDynamicDao.class.getName() + ".execute", dynamicSql);
    }
}
