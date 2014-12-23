package com.school.base.common.easyui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 对应EasyUI datagrid组件Json模型
 *
 * @author work
 */
public final class DataGridModel<T> {

    private int total;
    private List<DataGridRowModel> rows = new ArrayList<DataGridRowModel>();

    public DataGridModel() {
    }

    public DataGridModel(List<T> rowObjs) {
        this(rowObjs, rowObjs.size());
    }

    public DataGridModel(List<T> rowObjs, int total) {
        this.setRowObjects(rowObjs);
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataGridRowModel> getRows() {
        return rows;
    }

    public void setRows(List<DataGridRowModel> rows) {
        this.rows = rows;
    }

    public void setRowObjects(List<T> rowObjs) {
        List<DataGridRowModel> list = new ArrayList<DataGridRowModel>();
        for (Object obj : rowObjs) {
            DataGridRowModel row = new DataGridRowModel();
            row.setObject(obj);
            list.add(row);
        }
        this.setRows(list);
    }

    public DataGridModel<T> addRow(DataGridRowModel row) {
        this.rows.add(row);
        return this;
    }

    public DataGridModel<T> addRowObject(Object obj) {
        DataGridRowModel row = new DataGridRowModel();
        row.setObject(obj);
        this.addRow(row);
        return this;
    }

    public void sort(final String... attributes) {
        List<DataGridRowModel> dataGridRowModels = this.getRows();
        Collections.sort(dataGridRowModels, new Comparator<DataGridRowModel>() {
            @Override
            public int compare(DataGridRowModel o1, DataGridRowModel o2) {
                for (String attribute : attributes) {
                    Object v1 = o1.get(attribute);
                    Object v2 = o2.get(attribute);
                    
                    if (v1.equals(v2)) {
                        continue;
                    } else {
                        if (v1 instanceof String) {
                            return ((String) v1).compareToIgnoreCase(((String) v2));
                        } else {
                            throw new UnsupportedOperationException(attribute + "：不支持该属性的排序。");
                        }
                    }
                }
                return 0;
            }
        });
    }
}
