package com.suspend.querybuilder;

import com.suspend.reflection.ColumnMetadata;
import com.suspend.reflection.TableMetadata;

public class DefaultQueryBuilder implements QueryBuilder {

    private final StringBuilder builder = new StringBuilder();
    private final TableMetadata modelMetadata;

    public DefaultQueryBuilder(TableMetadata modelMetadata) {
        this.modelMetadata = modelMetadata;
    }

    @Override
    public String build() {
        return builder.toString();
    }

    @Override
    public QueryBuilder select() {
        builder.append("SELECT ");
        builder.append(String.join(
                        ", ",
                        modelMetadata.getColumns().stream().map(ColumnMetadata::getName).toList()));
        builder.append(" FROM ");
        builder.append(modelMetadata.getTableName());

        return this;
    }

    @Override
    public QueryBuilder insert() {
        builder.append("INSERT INTO ");
        builder.append(modelMetadata.getTableName());
        builder.append(" (");
        builder.append(String.join(
                        ", ",
                        modelMetadata.getColumns().stream().map(ColumnMetadata::getName).toList()));
        builder.append(") ");
        builder.append(" VALUES (");
        builder.append(String.join(
                        ", ",
                        modelMetadata.getColumns().stream().map(column -> "?").toList()));
        builder.append(")");

        return this;
    }

    @Override
    public QueryBuilder update() {
        builder.append("UPDATE ");
        builder.append(modelMetadata.getTableName());
        builder.append(" SET ");
        builder.append(String.join(
                        ", ",
                        modelMetadata.getColumns().stream().map(column -> column.getName() + " = ?").toList()));
        return this;
    }
}
