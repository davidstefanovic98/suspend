package com.suspend.querybuilder;

import com.suspend.reflection.TableMetadata;
import com.suspend.util.QueryBuilderUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Join<S extends TableMetadata, T extends TableMetadata> {

    private String sourceTable;
    private String sourceAlias;
    private String targetAlias;
    private String sourceColumnName;
    private String targetColumnName;
    private String targetTable;
    private Set<String> fields;
    private String joinType;
    private String aggregateTableName;

    private Join(){}

    public static <S extends TableMetadata, T extends TableMetadata> Join<S, T> create(
            String sourceTable,
            String sourceColumnName,
            String targetColumnName,
            String targetTable,
            Set<String> fields,
            String joinType,
            String aggregateTableName) {
        Join<S, T> self = new Join<>();
        self.sourceTable = sourceTable;
        self.sourceColumnName = sourceColumnName;
        self.targetColumnName = targetColumnName;
        self.targetTable = targetTable;
        self.fields = fields;
        self.joinType = joinType;
        self.aggregateTableName = aggregateTableName;
        return self;
    }


    public Set<String> getFields() {
        return fields
                .stream()
                .map(f -> String.format("%s.%s", QueryBuilderUtil.getAlias(targetTable), f))
                .collect(Collectors.toSet());
    }

    public String getSQL() {
        if (!aggregateTableName.isEmpty())
            return String.format("%s JOIN %s as %s ON %s", joinType, aggregateTableName, QueryBuilderUtil.getAlias(aggregateTableName), getJoinColumns(aggregateTableName));
        return String.format("%s JOIN %s as %s ON %s", joinType, targetTable, QueryBuilderUtil.getAlias(targetTable), getJoinColumns(targetTable));
    }

    private String getJoinColumns(String targetTable) {
        return String.format("%s.%s = %s.%s", QueryBuilderUtil.getAlias(sourceTable), sourceColumnName, QueryBuilderUtil.getAlias(targetTable), targetColumnName);
    }
}
