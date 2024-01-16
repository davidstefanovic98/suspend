package com.suspend.querybuilder;

import com.suspend.reflection.TableMetadata;

import java.util.List;

public class Join<S extends TableMetadata, T extends TableMetadata> {

    private String sourceTable;
    private String sourceAlias;
    private String targetAlias;
    private String sourceColumnName;
    private String targetColumnName;
    private String targetTable;
    private List<String> fields;

    private Join(){}

    public static <S extends TableMetadata, T extends TableMetadata> Join<S, T> create(
            String sourceTable,
            String sourceColumnName,
            String targetColumnName,
            String targetTable,
            List<String> fields) {
        Join<S, T> self = new Join<>();
        self.sourceTable = sourceTable;
        self.sourceColumnName = sourceColumnName;
        self.targetColumnName = targetColumnName;
        self.targetTable = targetTable;
        self.fields = fields;
        return self;
    }

    public List<String> getFields() {
        return fields.stream().map(f -> String.format("%s.%s", targetTable, f)).toList();
    }

    public String getSQL() {
        return String.format("LEFT JOIN %s ON %s", targetTable, getJoinColumns(targetTable));
    }

    private String getJoinColumns(String targetTable) {
        return String.format("%s.%s = %s.%s", sourceTable, sourceColumnName, targetTable, targetColumnName);
    }
}