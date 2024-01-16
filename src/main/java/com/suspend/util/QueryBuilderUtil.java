package com.suspend.util;

import com.suspend.annotation.JoinColumn;
import com.suspend.annotation.OneToMany;
import com.suspend.exception.AnnotationMissingException;
import com.suspend.querybuilder.Join;
import com.suspend.reflection.ColumnMetadata;
import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryBuilderUtil {

    private QueryBuilderUtil() {
    }

    public static List<Join<?, ?>> getJoins(TableMetadata tableMetadata, Set<TableMetadata> visited) {
        List<Join<?, ?>> result = new ArrayList<>();

        tableMetadata.getManyToOneColumns().forEach(column -> {
            Class<?> type = column.getType();
            TableMetadata metadata = new TableMapper().getMetadata(type);
            if (visited.contains(metadata)) {
                return;
            }
            visited.add(metadata);
            JoinColumn joinColumn = column.getAnnotations().stream()
                    .filter(annotation -> annotation instanceof JoinColumn)
                    .map(annotation -> (JoinColumn) annotation)
                    .findFirst()
                    .orElse(null);

            if (joinColumn == null) {
                throw new AnnotationMissingException(String.format("The @JoinColumn annotation is missing from the field '%s' in the class '%s'.", column.getName(), tableMetadata.getClazz().getName()));
            }
            Set<String> uniqueColumns = Stream.concat(metadata.getIdColumns().stream(), metadata.getColumns().stream())
                    .map(ColumnMetadata::getColumnName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            // If the referenced column is not specified, it should use primary key of the referenced table
            // Since primary key is not yet implemented, it will just the first column that has annotation @Id
            Join<?, ?> join = Join.create(
                    tableMetadata.getTableName(),
                    joinColumn.name(),
                    !joinColumn.referencedColumnName().isEmpty()
                            ? joinColumn.referencedColumnName()
                            : metadata.getIdColumns().get(0).getColumnName(),
                    metadata.getTableName(),
                    new ArrayList<>(uniqueColumns),
                    "LEFT");
            result.add(join);
        });

        tableMetadata.getOneToManyColumns().forEach(oneToManyColumn -> {
            Class<?> type = oneToManyColumn.getType();
            TableMetadata metadata = new TableMapper().getMetadata(type);
            if (visited.contains(metadata)) {
                return;
            }
            visited.add(metadata);
            OneToMany oneToMany = oneToManyColumn.getAnnotations().stream()
                    .filter(annotation -> annotation instanceof OneToMany)
                    .map(annotation -> (OneToMany) annotation)
                    .findFirst()
                    .orElse(null);

            if (oneToMany != null) {
                String mappedBy = oneToMany.mappedBy();
                // Find the corresponding @ManyToOne column
                Optional<ColumnMetadata> matchingManyToOneColumn = metadata.getManyToOneColumns().stream()
                        .filter(manyToOneColumn -> mappedBy.equals(manyToOneColumn.getName()))
                        .findFirst();

                matchingManyToOneColumn.ifPresent(column -> {
                    JoinColumn joinColumn = column.getAnnotations().stream()
                            .filter(annotation -> annotation instanceof JoinColumn)
                            .map(annotation -> (JoinColumn) annotation)
                            .findFirst()
                            .orElse(null);

                    if (joinColumn == null) {
                        throw new AnnotationMissingException(String.format("The @JoinColumn annotation is missing from the field '%s' in the class '%s'.", column.getName(), tableMetadata.getClazz().getName()));
                    }
                    Set<String> uniqueColumns = Stream.concat(metadata.getIdColumns().stream(), metadata.getColumns().stream())
                            .map(ColumnMetadata::getColumnName)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    Join<?, ?> inverseJoin = Join.create(
                            tableMetadata.getTableName(),
                            joinColumn.referencedColumnName(),
                            joinColumn.name(),
                            metadata.getTableName(),
                            new ArrayList<>(uniqueColumns),
                            "INNER");
                    result.add(inverseJoin);
                });
            }
        });
        return result;
    }

    public static List<Join<?, ?>> getJoins(TableMetadata tableMetadata) {
        return getJoins(tableMetadata, new HashSet<>());
    }

    public static String getScopedNameFromMapping(String tableName, String columnName) {
        return String.format("%s.%s", tableName, columnName);
    }
}
