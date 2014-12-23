package com.school.base.service.impl.management;

import com.school.base.dao.face.IDynamicDao;
import com.school.base.dao.face.management.IAuditDao;
import com.school.base.dao.face.management.IAuditVariableDao;
import com.school.base.dao.mybatis.interceptor.audit.AuditConfig;
import com.school.base.dao.mybatis.interceptor.audit.AuditConfigColumn;
import com.school.base.dao.mybatis.interceptor.audit.AuditConfigTable;
import com.school.base.domain.management.Audit;
import com.school.base.domain.management.AuditVariable;
import com.school.base.service.face.ServiceException;
import com.school.base.service.face.management.IAuditService;
import com.school.base.service.face.management.IAuditVariableFilter;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * T_AUDIT( ) 服务实现
 *
 * @作者 work
 * @创建时间 2012-12-11 14:58:32
 */
@Service
public class AuditService implements IAuditService {

    @Autowired
    private AuditConfig auditConfig;
    @Autowired
    private IAuditDao auditDao;
    @Autowired
    private IAuditVariableDao auditVariableDao;
    @Autowired
    private IDynamicDao dynamicDao;
    private List<IAuditVariableFilter> auditVariableFilters;

    @PostConstruct
    public void init() {
        this.auditVariableFilters = new ArrayList<IAuditVariableFilter>();
        this.auditVariableFilters.add(new PasswordAuditVariableFilter());
    }

    public void setAuditVariableFilters(List<IAuditVariableFilter> auditVariableFilters) {
        this.auditVariableFilters = auditVariableFilters;
    }

    @Override
    public int count(Map<String, Object> queryParams) {
        queryParams = this.convertQueryParams(queryParams);
        if (queryParams == null) {
            return 0;
        }
        return this.auditDao.count(queryParams);
    }

    @Override
    public List<Audit> page(int start, int limit, Map<String, Object> queryParams) {
        queryParams = this.convertQueryParams(queryParams);
        if (queryParams == null) {
            return Collections.EMPTY_LIST;
        }
        List<Audit> audits = this.auditDao.page(start, limit, queryParams);
        this.fetchVariable(audits);
        return audits;
    }

    @Override
    public List<Audit> find(Map<String, Object> queryParams) {
        queryParams = this.convertQueryParams(queryParams);
        if (queryParams == null) {
            return Collections.EMPTY_LIST;
        }
        List<Audit> audits = this.auditDao.find(queryParams);
        this.fetchVariable(audits);
        return audits;
    }

    @Override
    public int generatePosition(Map<String, Object> queryParams) {
        return this.auditDao.generatePosition(queryParams);
    }

    @Override
    public Audit load(int id) {
        Audit audit = this.auditDao.load(id);
        this.fetchVariable(audit);
        return audit;
    }

    @Override
    @Transactional
    public int insert(Audit audit) {
        this.auditDao.insert(audit);
        return audit.getId();
    }

    @Override
    @Transactional
    public int update(Audit audit) {
        return this.auditDao.update(audit);
    }

    @Override
    @Transactional
    public int deletes(int[] ids) {
        return this.auditDao.deletes(ids);
    }

    private Map<String, Object> convertQueryParams(Map<String, Object> queryParams) {
        if (queryParams.containsKey("when_to")) {
            Date toDate = (Date) queryParams.get("when_to");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(toDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.SECOND, -1);
            queryParams.put("when_to", calendar.getTime());
        }

        Set<String> keys = queryParams.keySet();
        final String prefix = "variable_";
        Map variableQueryParams = new HashMap();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (key.startsWith(prefix)) {
                variableQueryParams.put(key.substring(prefix.length()), queryParams.get(key));
                it.remove();
            }
        }

        if (!variableQueryParams.isEmpty()) {
            List<AuditVariable> variables = this.auditVariableDao.find(variableQueryParams);
            List<Integer> auditIds = new ArrayList();
            for (AuditVariable variable : variables) {
                auditIds.add(variable.getAuditId());
            }
            if (!auditIds.isEmpty()) {
                queryParams.put("id_", auditIds);
                return queryParams;
            } else {
                return null;
            }
        } else {
            return queryParams;
        }
    }

    @Override
    public List<AuditVariable> getVariables(int auditId, Map<String, Object> queryParams) {
        Audit audit = this.load(auditId);
        Map map = new HashMap();
        map.put("auditId", auditId);
        List<AuditVariable> variables = this.auditVariableDao.find(map);
        //group the relation tabl
        AuditConfigTable configTable = this.auditConfig.getConfig(audit.getTbl());
        if (configTable != null && configTable.isRelationTable()) {
            String sql = "select ID from T_AUDIT where TBL = " + audit.getTbl() + " and when < " + audit.getWhen();
            List<Map> list = this.dynamicDao.selectList(sql);
            List<Integer> ids = new ArrayList();
            for (Map idMap : list) {
                ids.add(((BigDecimal) idMap.get("ID")).intValue());
            }
            queryParams.put("audit_id_", ids);
            List<AuditVariable> groupVariables = this.auditVariableDao.find(queryParams);
            variables.addAll(groupVariables);
        }
        //filters
        for (IAuditVariableFilter filter : this.auditVariableFilters) {
            for (AuditVariable variable : variables) {
                if (filter.hit(audit.getTbl(), variable.getFieldName())) {
                    filter.process(variable);
                }
            }
        }

        return this.fetchForeignKey(audit.getTbl(), variables);
    }

    private void fetchVariable(List<Audit> audits) {
        for (Audit audit : audits) {
            this.fetchVariable(audit);
        }
    }

    private void fetchVariable(Audit audit) {
        Map map = new HashMap();
        map.put("auditId", audit.getId());
        List<AuditVariable> variables = this.auditVariableDao.find(map);
        variables = this.fetchForeignKey(audit.getTbl(), variables);
        audit.setVariables(variables);
    }

    private List<AuditVariable> fetchForeignKey(String tbaleName, List<AuditVariable> variables) {
        List<AuditConfigColumn> configColumns = this.auditConfig.get(tbaleName);
        for (AuditConfigColumn configColumn : configColumns) {
            if (configColumn.getForeignTable() != null) {
                //collect foreignKeys
                List<Integer> foreignKeys = this.collectForeignKeys(variables, configColumn);
                //fetch foreign keys
                Map<Integer, String> map = this.fetchForeighValues(configColumn, foreignKeys);
                //patch the foreign values into AuditVariable
                Iterator<AuditVariable> it = variables.iterator();
                while (it.hasNext()) {
                    AuditVariable variable = it.next();
                    if (variable.getFieldName().equals(configColumn.getName())) {
                        this.patchForeignValues(variable, map);
                    }
                }
            }
        }
        return variables;
    }

    private List<Integer> collectForeignKeys(List<AuditVariable> variables, AuditConfigColumn configColumn) {
        List<Integer> foreignKeys = new ArrayList();
        for (AuditVariable variable : variables) {
            if (variable.getFieldName().equals(configColumn.getName())) {
                if (!StringUtils.isEmpty(variable.getOldValue())) {
                    foreignKeys.add(Integer.valueOf(variable.getOldValue()));
                }
                if (!StringUtils.isEmpty(variable.getNewValue())) {
                    foreignKeys.add(Integer.valueOf(variable.getNewValue()));
                }
            }
        }
        return foreignKeys;
    }

    private Map<Integer, String> fetchForeighValues(AuditConfigColumn configColumn, List<Integer> foreignKeys) {
        Map<Integer, String> map = new HashMap();
        String sql = this.buildForeignValuesSql(configColumn, foreignKeys);
        List<Map> result = this.dynamicDao.selectList(sql);
        for (Map hashMap : result) {
            Integer id = ((BigDecimal) hashMap.get(configColumn.getForeignKeyColumn())).intValue();
            if (configColumn.getForeignNameColumn().equals("*")) {
                try {
                    map.put(id, new ObjectMapper().writeValueAsString(hashMap));
                } catch (IOException e) {
                    throw new ServiceException(e);
                }
            } else {
                Object obj = hashMap.get(configColumn.getForeignNameColumn());
                String name = obj == null ? id + "" : obj.toString();
                map.put(id, name);
            }
        }
        return map;
    }

    private String buildForeignValuesSql(AuditConfigColumn configColumn, List<Integer> foreignKeys) {
        StringBuilder builder = new StringBuilder();
        builder.append("select ");
        if (!configColumn.getForeignNameColumn().equals("*")) {
            builder.append(configColumn.getForeignKeyColumn());
            builder.append(", ");
        }
        builder.append(configColumn.getForeignNameColumn());
        builder.append(" from ");
        builder.append(configColumn.getForeignTable());
        builder.append(" where ");
        for (int i = 0; i < foreignKeys.size(); i++) {
            Integer valueId = foreignKeys.get(i);
            builder.append(" ").append(configColumn.getForeignKeyColumn()).append(" = ").append(valueId);
            if (i != foreignKeys.size() - 1) {
                builder.append(" or ");
            }
        }
        return builder.toString();
    }

    private void patchForeignValues(AuditVariable variable, Map<Integer, String> map) {
        //patch old value
        if (!StringUtils.isEmpty(variable.getOldValue())) {
            String oldValue = map.get(Integer.valueOf(variable.getOldValue()));
            if (!StringUtils.isEmpty(oldValue)) {
                variable.setOldValue(oldValue);
            }
        }
        //patch new value
        if (!StringUtils.isEmpty(variable.getNewValue())) {
            String newValue = map.get(Integer.valueOf(variable.getNewValue()));
            if (!StringUtils.isEmpty(newValue)) {
                variable.setNewValue(newValue);
            }
        }
    }
}
