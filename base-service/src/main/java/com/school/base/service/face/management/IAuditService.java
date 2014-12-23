package com.school.base.service.face.management;

import java.util.List;
import java.util.Map;

import com.school.base.domain.management.Audit;
import com.school.base.domain.management.AuditVariable;

/**
 * T_AUDIT( ) 服务接口
 *
 * @作者 work
 * @创建时间 2012-12-11 14:58:32
 */
public interface IAuditService {

    /**
     * 模糊统计符合查询条件的记录总数
     *
     * @param queryParams 查询参数
     * @return 记录总数
     */
    int count(Map<String, Object> queryParams);

    /**
     * 模糊获取符合查询条件的分页记录
     *
     * @param start 记录起始位置
     * @param limit 记录条数
     * @param queryParams 查询参数
     * @return 记录列表
     */
    List<Audit> page(int start, int limit, Map<String, Object> queryParams);

    /**
     * 精确获取符合查询条件的记录
     *
     * @param queryParams 查询参数
     * @return 记录列表
     */
    List<Audit> find(Map<String, Object> queryParams);

    /**
     * 精确生成将要插入的记录的序号
     *
     * @param queryParams 查询参数
     * @return 序号
     */
    int generatePosition(Map<String, Object> queryParams);

    /**
     * 根据标识获取记录
     *
     * @param id 标识
     * @return 记录
     */
    Audit load(int id);

    /**
     * 插入记录
     *
     * @param domainInstance Audit实例
     * @return 被插入的记录标识
     */
    int insert(Audit domainInstance);

    /**
     * 修改记录
     *
     * @param domainInstance Audit实例
     * @return 被修改的记录数
     */
    int update(Audit domainInstance);

    /**
     * 删除多条
     *
     * @param ids 标识列表
     * @return 被删除的记录数
     */
    int deletes(int[] ids);

    /**
     * 获取审计变量
     *
     * @param id
     * @return
     */
    List<AuditVariable> getVariables(int auditId, Map<String, Object> queryParams);
}
