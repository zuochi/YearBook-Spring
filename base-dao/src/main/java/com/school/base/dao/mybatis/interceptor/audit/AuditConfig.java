package com.school.base.dao.mybatis.interceptor.audit;

import com.school.base.dao.mybatis.interceptor.InterceptorUtil;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.reflect.FieldUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

/**
 *
 * @author work
 */
@Component
public class AuditConfig {

    private List<AuditConfigTable> configTables;
    private String location = "/config_audit.xml";

    @PostConstruct
    public void init() {
        try {
            this.configTables = new ArrayList<AuditConfigTable>();
            URL url = AuditConfigTable.class.getResource(this.location);
            SAXReader reader = new SAXReader();
            Document document = reader.read(url);
            List<Element> tables = document.selectNodes("//table");
            for (Element table : tables) {
                AuditConfigTable configTable = new AuditConfigTable();
                List<Element> columns = table.elements("column");
                List<AuditConfigColumn> configColumns = new ArrayList();
                configTable.setName(table.attributeValue("name"));
                configTable.setDomain(table.attributeValue("domain"));
                configTable.setRelationTable(!"false".equals(table.attributeValue("relationTable")));
                configTable.setAuditConfigColumns(configColumns);
                this.configTables.add(configTable);
                for (Element column : columns) {
                    AuditConfigColumn configColumn = new AuditConfigColumn();
                    configColumn.setName(column.attributeValue("name"));
                    configColumn.setField(column.attributeValue("field"));
                    configColumn.setDescription(column.attributeValue("description"));
                    configColumn.setForeignTable(column.attributeValue("foreignTable"));
                    configColumn.setForeignKeyColumn(column.attributeValue("foreignKeyColumn"));
                    configColumn.setForeignNameColumn(column.attributeValue("foreignNameColumn"));
                    configColumn.setForeignSql(column.attributeValue("foreignSql"));
                    configColumns.add(configColumn);
                }
            }

            //check if valid config
            for (AuditConfigTable configTable : this.configTables) {
                String domain = configTable.getDomain();
                try {
                    Class cls = Class.forName(domain);
                    List<AuditConfigColumn> configColumns = configTable.getAuditConfigColumns();
                    for (AuditConfigColumn configColumn : configColumns) {
                        String fieldName = configColumn.getField();
                        Field field = FieldUtils.getDeclaredField(cls, fieldName, true);
                        if (field == null) {
                            throw new RuntimeException("audit_config fails, field is not found in the domain class,"
                                    + " fieldName = " + fieldName + ", domain class = " + domain);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("audit_config fails, domain class not found.", e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean domainEnable(Object parameter) {
        Class cls = parameter.getClass();
        String clsName = cls.getName();
        for (AuditConfigTable configTable : this.configTables) {
            if (clsName.equals(configTable.getDomain())) {
                return true;
            }
        }
        return false;
    }

    public boolean sqlEnable(String sql) {
        String tableName = InterceptorUtil.getTable(sql);
        for (AuditConfigTable configTable : this.configTables) {
            if (configTable.getName().equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    public boolean variableEnable(String tableName) {
        return !this.get(tableName).isEmpty();
    }

    public AuditConfigTable getConfig(String tableName) {
        for (AuditConfigTable configTable : this.configTables) {
            if (configTable.getName().equals(tableName)) {
                return configTable;
            }
        }
        return null;
    }

    public List<AuditConfigColumn> get(String tableName) {
        for (AuditConfigTable configTable : this.configTables) {
            if (configTable.getName().equals(tableName)) {
                return configTable.getAuditConfigColumns();
            }
        }
        return Collections.EMPTY_LIST;
    }

    public AuditConfigColumn get(String table, String column) {
        List<AuditConfigColumn> configColumns = this.get(table);
        for (AuditConfigColumn configColumn : configColumns) {
            if (column.equals(configColumn.getName())) {
                return configColumn;
            }
        }
        return null;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setConfigTables(List<AuditConfigTable> configTables) {
        this.configTables = configTables;
    }
}
