package com.suspend.util;

import com.suspend.annotation.JoinColumn;
import com.suspend.annotation.JoinTable;
import com.suspend.annotation.ManyToMany;
import com.suspend.annotation.OneToMany;
import com.suspend.exception.AnnotationMissingException;
import com.suspend.querybuilder.Join;
import com.suspend.reflection.ColumnMetadata;
import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryBuilderUtil {

    private QueryBuilderUtil() {
    }

    public static List<Join<?, ?>> getJoins(TableMetadata tableMetadata, Set<String> recursionStack) {
        List<Join<?, ?>> result = new ArrayList<>();
        tableMetadata.getManyToOneColumns().forEach(column -> {

            Class<?> type = column.getType();
            TableMetadata metadata = new TableMapper().getMetadata(type);
            String relationshipKey = tableMetadata.getTableName() + "." + metadata.getTableName();
            boolean found = recursionStack.stream().anyMatch(key -> {
                String[] splittedKey = QueryBuilderUtil.splitRelationshipKey(key, ".");
                return metadata.getTableName().equals(splittedKey[0]) && tableMetadata.getTableName().equals(splittedKey[1]);
            });

            if (!recursionStack.add(relationshipKey) || found) {
                return;
            }

            JoinColumn joinColumn = column.getAnnotations().stream()
                    .filter(annotation -> annotation instanceof JoinColumn)
                    .map(annotation -> (JoinColumn) annotation)
                    .findFirst()
                    .orElse(null);

            if (joinColumn == null) {
                throw new AnnotationMissingException(String.format("The @JoinColumn annotation is missing from the field '%s' in the class '%s'.", column.getName(), tableMetadata.getClazz().getName()));
            }
            Join<?, ?> join = createJoin(tableMetadata, column, metadata, joinColumn, "LEFT");
            result.add(join);
            result.addAll(getJoins(metadata, recursionStack));
        });

        tableMetadata.getOneToManyColumns().forEach(oneToManyColumn -> {
            Class<?> type = oneToManyColumn.getType();
            TableMetadata metadata = new TableMapper().getMetadata(type);
            String relationshipKey = tableMetadata.getTableName() + "." + metadata.getTableName();
            boolean found = recursionStack.stream().anyMatch(key -> {
                String[] splittedKey = QueryBuilderUtil.splitRelationshipKey(key, ".");
                return metadata.getTableName().equals(splittedKey[0]) && tableMetadata.getTableName().equals(splittedKey[1]);
            });

            if (!recursionStack.add(relationshipKey) || found) {
                return;
            }
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
                    Join<?, ?> inverseJoin = createJoin(tableMetadata, matchingManyToOneColumn.get(), metadata, joinColumn, "INNER");
                    result.add(inverseJoin);
                    result.addAll(getJoins(metadata, recursionStack));
                });
            }
        });

        tableMetadata.getManyToManyColumns().forEach(manyToManyColumn -> {
            Class<?> type = manyToManyColumn.getType();
            TableMetadata metadata = new TableMapper().getMetadata(type);

            String relationshipKey = tableMetadata.getTableName() + "." + metadata.getTableName();
            boolean found = recursionStack.stream().anyMatch(key -> {
                String[] splittedKey = QueryBuilderUtil.splitRelationshipKey(key, ".");
                return metadata.getTableName().equals(splittedKey[0]) && tableMetadata.getTableName().equals(splittedKey[1]);
            });

            if (!recursionStack.add(relationshipKey) || found) {
                return;
            }

            ManyToMany manyToMany = manyToManyColumn.getAnnotations().stream()
                    .filter(annotation -> annotation instanceof ManyToMany)
                    .map(annotation -> (ManyToMany) annotation)
                    .findFirst()
                    .orElse(null);

            if (manyToMany != null) {
                Optional<Field> matchingManyToManyColumn = Arrays.stream(type.getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(ManyToMany.class))
                        .findFirst();
                matchingManyToManyColumn.ifPresent(column -> {
                    JoinTable matchingJoinTable = column.getAnnotation(JoinTable.class);

                    JoinTable joinTable = manyToManyColumn.getAnnotations().stream()
                            .filter(annotation -> annotation instanceof JoinTable)
                            .map(annotation -> (JoinTable) annotation)
                            .findFirst()
                            .orElse(null);

                    Set<String> joinTableColumnsName = new HashSet<>();
                    Set<String> inverseJoinTableColumnsName = new HashSet<>();
                    Set<String> joinTableColumnsRef = new HashSet<>();
                    Set<String> inverseJoinTableColumnsRef = new HashSet<>();

                    Set<String> matchingJoinTableColumnsName = new HashSet<>();
                    Set<String> inverseMatchingJoinTableColumnsName = new HashSet<>();
                    Set<String> matchingJoinTableColumnsRef = new HashSet<>();
                    Set<String> inverseMatchingJoinTableColumnsRef = new HashSet<>();


                    if (joinTable != null) {
                        joinTableColumnsName = Arrays.stream(joinTable.joinColumns())
                                .map(JoinColumn::name)
                                .collect(Collectors.toSet());
                        inverseJoinTableColumnsName = Arrays.stream(joinTable.inverseJoinColumns())
                                .map(JoinColumn::name)
                                .collect(Collectors.toSet());

                        joinTableColumnsRef = Arrays.stream(joinTable.joinColumns())
                                .map(JoinColumn::referencedColumnName)
                                .collect(Collectors.toSet());
                        inverseJoinTableColumnsRef = Arrays.stream(joinTable.inverseJoinColumns())
                                .map(JoinColumn::referencedColumnName)
                                .collect(Collectors.toSet());
                    } else if (matchingJoinTable != null) {
                        matchingJoinTableColumnsName = Arrays.stream(matchingJoinTable.joinColumns())
                                .map(JoinColumn::name)
                                .collect(Collectors.toSet());
                        inverseMatchingJoinTableColumnsName = Arrays.stream(matchingJoinTable.inverseJoinColumns())
                                .map(JoinColumn::name)
                                .collect(Collectors.toSet());

                        matchingJoinTableColumnsRef = Arrays.stream(matchingJoinTable.joinColumns())
                                .map(JoinColumn::referencedColumnName)
                                .collect(Collectors.toSet());
                        inverseMatchingJoinTableColumnsRef = Arrays.stream(matchingJoinTable.inverseJoinColumns())
                                .map(JoinColumn::referencedColumnName)
                                .collect(Collectors.toSet());
                    }

                    Set<String> uniqueColumns = Stream.concat(metadata.getIdColumns().stream(), metadata.getColumns().stream())
                            .map(ColumnMetadata::getColumnName)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    Join<?, ?> join = Join.create(
                            tableMetadata.getTableName(),
                            joinTable == null ? matchingJoinTableColumnsRef.iterator().next() : inverseJoinTableColumnsRef.iterator().next(),
                            joinTable == null ? inverseMatchingJoinTableColumnsName.iterator().next() : joinTableColumnsName.iterator().next(),
                            metadata.getTableName(),
                            uniqueColumns,
                            "LEFT",
                            joinTable == null ? matchingJoinTable.name() : joinTable.name()
                    );
                    Join<?, ?> inverseJoin = Join.create(
                            joinTable == null ? matchingJoinTable.name(): joinTable.name(),
                            joinTable == null ? matchingJoinTableColumnsName.iterator().next() : inverseJoinTableColumnsName.iterator().next(),
                            joinTable == null ? inverseMatchingJoinTableColumnsRef.iterator().next() : joinTableColumnsRef.iterator().next(),
                            metadata.getTableName(),
                            uniqueColumns,
                            "LEFT",
                            metadata.getTableName()
                    );
                    result.add(join);
                    result.add(inverseJoin);
                    result.addAll(getJoins(metadata, recursionStack));
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

    public static String getAlias(String tableName) {
        String hash = Integer.valueOf(Math.abs(tableName.hashCode())).toString().replace(",", "");
        return String.format("%s_%s", tableName, hash);
    }

    private static Join<?, ?> createJoin(TableMetadata source, ColumnMetadata sourceColumn, TableMetadata target, JoinColumn joinColumn, String joinType) {
        Set<String> uniqueColumns = Stream.concat(target.getIdColumns().stream(), target.getColumns().stream())
                .map(ColumnMetadata::getColumnName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return Join.create(
                source.getTableName(),
                joinColumn.name(),
                !joinColumn.referencedColumnName().isEmpty()
                        ? joinColumn.referencedColumnName()
                        : target.getIdColumns().get(0).getColumnName(),
                target.getTableName(),
                uniqueColumns,
                joinType,
                ""
        );
    }

    public static String[] splitRelationshipKey(String relationshipKey, String delimiter) {
        if (Objects.equals(delimiter, ".")) {
            return relationshipKey.split("\\.");
        }
        return relationshipKey.split("_");
    }
}
