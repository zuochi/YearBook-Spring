package com.school.base.dao.face;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author work
 */
public interface IDynamicDao {

    HashMap selectOne(String dynamicSql);

    List<Map> selectList(String dynamicSql);

    void execute(String dynamicSql);
}
