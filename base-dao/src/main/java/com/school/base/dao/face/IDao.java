/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.school.base.dao.face;

import java.util.List;
import java.util.Map;

/**
 * 数据访问层基本接口
 *
 * @author work
 */
public interface IDao<T> {
	/**
     * 统计符合查询条件的记录总数（模糊查询）
     * <p>
     * queryParams中的每一个参数都是key/value对。
     * 如果key后面带有下横线“_”，则表示该参数是一个List<Object>。
     * 如果queryParams含有key为sort（和order）的参数，则sort（和order）用以排序。
     * 例如，queryParams值为
     * {sex:1,birthday_:[2012-01-01,2012-01-31],status_:[1,2,3],category:["cat","bird"],sort:"name",order:"asc"}，“2012-01-01”的jdbcType为DATE
     * 则构成的查询条件为：
     * where 1=1 and SEX=1 and BIRTHDAY>=2012-01-01 and BIRTHDAY<=2012-01-31 and STATUS in (1,2,3) and (upper(CATEGORY) like '%' || upper('cat') || '%' or upper(CATEGORY) like '%' || upper('bird') || '%') order by name asc
     * </p>
     * @param queryParams 查询参数
     * @return 记录总数
     */
    int count(Map<String, Object> queryParams);
    
    /**
     * 获取符合查询条件的分页记录（模糊查询）
     * @param start 记录起始位置
     * @param limit 记录条数
     * @param queryParams 查询参数，见方法count的queryParams
     * @return 记录列表
     */
    List<T> page(int page, int limit, Map<String, Object> queryParams);   
    
    /**
     * 获取符合查询条件的记录（精确查询）
     * <p>
     * queryParams中的每一个参数都是key/value对。
     * 如果key后面带有下横线“_”，则表示该参数是一个List<Object>。
     * 如果queryParams含有key为sort（和order）的参数，则sort（和order）用以排序。
     * 例如，queryParams值为
     * {sex:1,birthday_:[2012-01-01,2012-01-31],status_:[1,2,3],category:["cat","bird"],sort:"name",order:"asc"}，“2012-01-01”的jdbcType为DATE
     * 则构成的查询条件为：
     * where 1=1 and SEX=1 and BIRTHDAY>=2012-01-01 and BIRTHDAY<=2012-01-31 and STATUS in (1,2,3) and upper(CATEGORY) in (upper('cat'),upper('bird')) order by name asc
     * </p>
     * @param queryParams 查询参数
     * @return 记录列表
     */
    List<T> find(Map<String, Object> queryParams);
    
    /**
     * 生成将要插入的记录的序号（精确查询）
     * @param queryParams 查询参数，见方法find的queryParams
     * @return 序号
     */
    int generatePosition(Map<String, Object> queryParams);

    /**
     * 根据标识获取记录
     * @param id 标识
     * @return 记录
     */
    T load(int id);

    /**
     * 插入记录
     * @param record
     * @return 被插入的记录数
     */
    int insert(T record);

    /**
     * 修改记录
     * @param record
     * @return 被修改的记录数
     */
    int update(T record);

    /**
     * 删除一条
     * <p>
     * 如果是树型结构的数据，则把其下的级联记录也删除
     * </p>
     * @param id 标识
     * @return 被删除的记录数
     */
    int delete(int id);
    
    /**
     * 删除多条
     * @param ids 标识列表
     * @return 被删除的记录数
     */
    int deletes(List<Integer> ids);
    
    /**
     * 删除多条
     * @param ids 标识列表
     * @return 被删除的记录数
     * @deprecated
     */
    int deletes(int[] ids);
    
    /**
     * 删除多条（精确查询）
     * @param queryParams 查询参数，见方法find的queryParams
     * @return 被删除的记录数
     */
    int deletes(Map<String, Object> queryParams);
}
