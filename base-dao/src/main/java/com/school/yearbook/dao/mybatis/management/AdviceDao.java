package com.school.yearbook.dao.mybatis.management;

import org.springframework.stereotype.Repository;

import com.school.base.dao.mybatis.Dao;
import com.school.yearbook.dao.face.management.IAdivceDao;
import com.school.yearbook.domain.management.Advice;

/**
 * advice(建议表)DAO实现
 * @author Gilbert
 *
 * 2014年12月22日
 */
@Repository
public class AdviceDao extends Dao<Advice> implements IAdivceDao {
    
}