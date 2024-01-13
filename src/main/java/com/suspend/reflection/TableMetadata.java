package com.suspend.reflection;

import java.util.List;

public class TableMetadata {
    private String tableName;
    private List<ColumnMetadata> columns;

    public TableMetadata() {}

    public TableMetadata(String tableName, List<ColumnMetadata> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnMetadata> getColumns() {
        return columns;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumns(List<ColumnMetadata> columns) {
        this.columns = columns;
    }
}
