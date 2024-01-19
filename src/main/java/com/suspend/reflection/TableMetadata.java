package com.suspend.reflection;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class TableMetadata {
    private String tableName;
    private String className;
    private List<ColumnMetadata> columns;
    private List<ColumnMetadata> idColumns;
    private List<ColumnMetadata> oneToManyColumns;
    private List<ColumnMetadata> manyToOneColumns;
    private List<ColumnMetadata> manyToManyColumns;
    private Class<?> clazz;

    public TableMetadata() {}

    public TableMetadata(
            String tableName,
            String className,
            List<ColumnMetadata> columns,
            List<ColumnMetadata> idColumns,
            List<ColumnMetadata> oneToManyColumns,
            List<ColumnMetadata> manyToOneColumns,
            List<ColumnMetadata> manyToManyColumns,
            Class<?> clazz) {
        this.tableName = tableName;
        this.className = className;
        this.columns = columns;
        this.idColumns = idColumns;
        this.oneToManyColumns = oneToManyColumns;
        this.manyToOneColumns = manyToOneColumns;
        this.manyToManyColumns = manyToManyColumns;
        this.clazz = clazz;
    }

    public String getTableName() {
        return tableName;
    }

    public String getClassName() {
        return className;
    }

    public Class<?> getClazz() {
        return clazz;
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

    public List<ColumnMetadata> getManyToManyColumns() {
        return manyToManyColumns;
    }

    public List<ColumnMetadata> getAllColumns() {
        return Stream.of(idColumns, columns, oneToManyColumns, manyToOneColumns, manyToManyColumns)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableMetadata that)) return false;
        return Objects.equals(getTableName(), that.getTableName()) && Objects.equals(getClassName(), that.getClassName()) && Objects.equals(getColumns(), that.getColumns()) && Objects.equals(getIdColumns(), that.getIdColumns()) && Objects.equals(getOneToManyColumns(), that.getOneToManyColumns()) && Objects.equals(getManyToOneColumns(), that.getManyToOneColumns()) && Objects.equals(getClazz(), that.getClazz());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTableName(), getClassName(), getColumns(), getIdColumns(), getOneToManyColumns(), getManyToOneColumns(), getClazz());
    }
}
