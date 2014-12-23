package com.school.base.dao.mybatis.interceptor;

import java.util.Map;

import com.school.base.dao.face.IDao;
import com.school.base.dao.mybatis.Dao;

import net.sf.ehcache.CacheManager;

/**
 * @author work
 */
public class ClearCacheInterceptor<T> implements DaoInsertInterceptor<T>, DaoUpdateInterceptor<T>, DaoDeleteInterceptor<T> {

    private Class<IDao>[] daoClasses;

    public ClearCacheInterceptor(Class<IDao>... daoClasses) {
        this.daoClasses = daoClasses;
    }

    @Override
    public void invoke(Dao dao, T obj) {
        this.clearCache();
    }

    @Override
    public void invoke(Dao dao, Map<String, Object> queryParams) {
        this.clearCache();
    }

    private void clearCache() {
        for (Class<IDao> cls : this.daoClasses) {
            CacheManager.create().clearAllStartingWith(cls.getName());
        }
    }
}
