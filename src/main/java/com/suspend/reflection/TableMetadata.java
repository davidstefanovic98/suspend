package com.suspend.reflection;

import java.util.List;

public class TableMetadata {
    private String tableName;
    private String className;
    private List<ColumnMetadata> columns;
    private List<ColumnMetadata> idColumns;
    private List<ColumnMetadata> oneToManyColumns;
    private List<ColumnMetadata> manyToOneColumns;

    public TableMetadata() {}

    public TableMetadata(String tableName, String className, List<ColumnMetadata> columns, List<ColumnMetadata> idColumns, List<ColumnMetadata> oneToManyColumns, List<ColumnMetadata> manyToOneColumns) {
        this.tableName = tableName;
        this.className = className;
        this.columns = columns;
        this.idColumns = idColumns;
        this.oneToManyColumns = oneToManyColumns;
        this.manyToOneColumns = manyToOneColumns;
    }

    public String getTableName() {
        return tableName;
    }

    public String getClassName() {
        return className;
    }

    public List<ColumnMetadata> getColumns() {
        return columns;
    }

    public List<ColumnMetadata> getIdColumns() {
        return idColumns;
    }

    public List<ColumnMetadata> getOneToManyColumns() {
        return oneToManyColumns;
    }

    public List<ColumnMetadata> getManyToOneColumns() {
        return manyToOneColumns;
    }
}
