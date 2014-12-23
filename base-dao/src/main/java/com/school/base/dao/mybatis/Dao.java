/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.school.base.dao.mybatis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.school.base.dao.face.DaoAccessException;
import com.school.base.dao.face.IDao;
import com.school.base.dao.mybatis.interceptor.DaoDeleteInterceptor;
import com.school.base.dao.mybatis.interceptor.DaoInsertInterceptor;
import com.school.base.dao.mybatis.interceptor.DaoUpdateInterceptor;

/**
 * 数据库访问层基本实现类
 *
 * @author work
 */
public abstract class Dao<T> extends SqlSessionDaoSupport implements IDao<T> {
    protected String mapperNamespace = this.detectMapper();
    private List<DaoInsertInterceptor<T>> insertInterceptors = new ArrayList<DaoInsertInterceptor<T>>();
    private List<DaoUpdateInterceptor<T>> updateInterceptors = new ArrayList<DaoUpdateInterceptor<T>>();
    private List<DaoDeleteInterceptor<T>> deleteInterceptors = new ArrayList<DaoDeleteInterceptor<T>>();

    @Override
    public int count(Map<String, Object> queryParams) {
        return (Integer) super.getSqlSession().selectOne(this.mapperNamespace + ".count", queryParams);
    }

    @Override
    public List<T> page(int start, int limit, Map<String, Object> queryParams) {
        RowBounds bounds = new RowBounds(start, limit);
        return super.getSqlSession().selectList(this.mapperNamespace + ".page", queryParams, bounds);
    }

    @Override
    public List<T> find(Map<String, Object> queryParams) {
        List<T> objs = super.getSqlSession().selectList(this.mapperNamespace + ".find", queryParams);
        return objs;
    }

    protected List<T> find(String name, Object queryParams) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(name, queryParams);
        return this.find(map);
    }
    
    @Override
    public int generatePosition(Map<String, Object> queryParams) {
    	return (Integer)super.getSqlSession().selectOne(this.mapperNamespace + ".generatePosition", queryParams);
    }

    @Override
    public T load(int id) {
    	if(id <= 0) {
    		throw new DaoAccessException("invalid arguments.");
    	}
        return super.getSqlSession().selectOne(this.mapperNamespace + ".load", id);
    }

    @Override
    public int insert(T record) {
        for (DaoInsertInterceptor<T> interceptor : this.insertInterceptors) {
            interceptor.invoke(this, record);
        }
        return super.getSqlSession().insert(this.mapperNamespace + ".insert", record);
    }

    @Override
    public int update(T record) {
        for (DaoUpdateInterceptor<T> interceptor : this.updateInterceptors) {
            interceptor.invoke(this, record);
        }
        return super.getSqlSession().update(this.mapperNamespace + ".update", record);
    }
    
    @Override
    public int delete(int id) {
    	if(id <= 0) {
    		throw new DaoAccessException("invalid arguments.");
    	}
    	Map<String, Object> queryParams = new HashMap<String, Object>();
    	queryParams.put("ancestor", id);
    	return this.deletes(queryParams);
    }
    
    @Override
    public int deletes(List<Integer> ids) {
    	if(ids == null || ids.size() == 0) {
    		throw new DaoAccessException("invalid arguments.");
    	}
    	Map<String, Object> queryParams = new HashMap<String, Object>();
    	queryParams.put("id_", ids);
    	return this.deletes(queryParams);
    	//return this.deletes(ArrayUtils.toPrimitive(ids.toArray(new Integer[ids.size()])));
    }
    
    @Override
    public int deletes(int[] ids) {
    	return this.deletes(Arrays.asList(ArrayUtils.toObject(ids)));
    }
    
    @Override
    public int deletes(Map<String, Object> queryParams) {
    	if(queryParams == null || queryParams.isEmpty()) {
    		throw new DaoAccessException("invalid arguments.");
    	}
    	for (DaoDeleteInterceptor<T> interceptor : this.deleteInterceptors) {
            interceptor.invoke(this, queryParams);
        }
    	return super.getSqlSession().delete(this.mapperNamespace + ".deletes", queryParams);
    }
	
    public void registerDaoInertInterceptor(DaoInsertInterceptor<T> interceptor) {
        this.insertInterceptors.add(interceptor);
    }

    public void removeDaoInertInterceptor(DaoInsertInterceptor<T> interceptor) {
        this.insertInterceptors.remove(interceptor);
    }

    public void registerDaoUpdateInterceptor(DaoUpdateInterceptor<T> interceptor) {
        this.updateInterceptors.add(interceptor);
    }

    public void removeDaoUpdateInterceptor(DaoUpdateInterceptor<T> interceptor) {
        this.updateInterceptors.remove(interceptor);
    }

    public void registerDaoDeleteInterceptor(DaoDeleteInterceptor<T> interceptor) {
        this.deleteInterceptors.add(interceptor);
    }

    public void removeDaoDeleteInterceptor(DaoDeleteInterceptor<T> interceptor) {
        this.deleteInterceptors.remove(interceptor);
    }

    /**
     * 自动扫描搜索mapper.xml的namespace
     * 
     * @return 
     */
    private String detectMapper() {
        Class dao = this.getClass();
        if (dao.isAnnotationPresent(Mapper.class)) {
            Mapper mapperNamespace = (Mapper) dao.getAnnotation(Mapper.class);
            return mapperNamespace.value();
        } else {
            List<Class> interfaces = ClassUtils.getAllInterfaces(dao);
            for (Class cls : interfaces) {
                List<Class> superClasses = ClassUtils.getAllInterfaces(cls);
                for (Class superCls : superClasses) {
                    if (superCls.equals(IDao.class)) {
                        return cls.getName();
                    }
                }
            }
            throw new IllegalStateException("继承Dao类的子类必须实现一个继承了IDao接口的接口。" + this.getClass().getName());
        }
    }
}
