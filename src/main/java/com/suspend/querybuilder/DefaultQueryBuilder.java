package com.suspend.querybuilder;

import com.suspend.exception.IncorrectTypeException;
import com.suspend.reflection.ColumnMetadata;
import com.suspend.reflection.TableMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class DefaultQueryBuilder implements QueryBuilder {

    private final Logger logger = LoggerFactory.getLogger(DefaultQueryBuilder.class);
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
    public QueryBuilder select(String... columns) {
        builder.append("SELECT ");

        if (columns.length == 0) {
            builder.append("*");
        } else {
            builder.append(String.join(
                    ", ",
                    columns));
        }
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
                        modelMetadata
                                .getColumns()
                                .stream()
                                .filter(column -> !modelMetadata.getIdColumns().contains(column))
                                .map(ColumnMetadata::getName)
                                .toList()));
        builder.append(") ");
        builder.append(" VALUES (");

        String values = modelMetadata
                .getColumns()
                .stream()
                .filter(column -> !modelMetadata.getIdColumns().contains(column))
                .map(column -> getFormattedFieldValue(column, column.getValue()))
                .collect(Collectors.joining(", "));

        builder.append(String.join(
                        ", ",
                       values));
        builder.append(")");

        return this;
    }


    @Override
    public QueryBuilder update() {
        ColumnMetadata id = modelMetadata.getIdColumns().get(0);
        builder.append("UPDATE ");
        builder.append(modelMetadata.getTableName());
        builder.append(" SET ");

        builder.append(String.join(
                        ", ",
                        modelMetadata
                                .getColumns()
                                .stream()
                                .map(column -> column.getColumnName() + " = " + getFormattedFieldValue(column, column.getValue()))
                                .toList()));

        builder.append(" WHERE ");
        builder.append(id.getColumnName());
        builder.append(" = ");
        builder.append(getFormattedFieldValue(id, id.getValue()));
        return this;
    }

    @Override
    public <T> QueryBuilder byId(T id) {
        ColumnMetadata columnMetadata = modelMetadata.getIdColumns().get(0);

        if (!columnMetadata.getType().isAssignableFrom(id.getClass())) {
            throw new IncorrectTypeException("The type of the id does not match the type of the column.");
        }

        this.select();
        builder.append(" WHERE ");
        builder.append(columnMetadata.getColumnName());
        builder.append(" = ");
        builder.append(getFormattedFieldValue(columnMetadata, id));
        return this;
    }

    @Override
    public <T> QueryBuilder deleteById(T id) {
        builder.append("DELETE FROM ");
        builder.append(modelMetadata.getTableName());
        builder.append(" WHERE ");
        builder.append(modelMetadata.getIdColumns().get(0).getColumnName());
        builder.append(" = ");
        builder.append(getFormattedFieldValue(modelMetadata.getIdColumns().get(0), id));
        return this;
    }

    @Override
    public QueryBuilder where(String... whereParams) {
        return null;
    }

    @Override
    public QueryBuilder where(Map<String, Object> params) {
        return null;
    }

    // Jesus Christ...
    private String getFormattedFieldValue(ColumnMetadata field, Object value) {
        if (value == null) {
            return "NULL";
        } else if (String.class.isAssignableFrom(field.getType())) {
            return String.format("'%s'", value);
        } else if (Byte.class.isAssignableFrom(field.getType())) {
            return value.toString();
        } else if (Short.class.isAssignableFrom(field.getType())) {
            return value.toString();
        } else if (Integer.class.isAssignableFrom(field.getType())) {
            return value.toString();
        } else if (Float.class.isAssignableFrom(field.getType())) {
            return value.toString();
        } else if (Double.class.isAssignableFrom(field.getType())) {
            return value.toString();
        } else if (Boolean.class.isAssignableFrom(field.getType())) {
            return value.toString();
        } else if (Long.class.isAssignableFrom(field.getType())) {
            return value.toString();
        } else if (Character.class.isAssignableFrom(field.getType())) {
            return value.toString();
        } else if (field.getType().isPrimitive()) {
            return value.toString();
        } else {
            return value.toString();
        }
    }
}
