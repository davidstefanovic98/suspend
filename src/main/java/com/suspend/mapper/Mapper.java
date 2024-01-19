package com.suspend.mapper;

import com.suspend.annotation.ManyToMany;
import com.suspend.annotation.ManyToOne;
import com.suspend.annotation.OneToMany;
import com.suspend.configuration.Configuration;
import com.suspend.entitymanager.EntityContainer;
import com.suspend.entitymanager.EntityReference;
import com.suspend.exception.InvalidMappingException;
import com.suspend.reflection.ColumnMetadata;
import com.suspend.util.QueryBuilderUtil;
import com.suspend.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Mapper<T> {

    private final List<Map<String, Object>> data;
    private final Class<T> clazz;
    private final Logger logger = LoggerFactory.getLogger(Mapper.class);
    private final Configuration configuration;
    private final Map<String, Set<Object>> mappedInstancesMap = new HashMap<>();

    public Mapper(Class<T> clazz, List<Map<String, Object>> data) {
        this.data = data;
        this.clazz = clazz;
        configuration = Configuration.getInstance();
    }

    public List<T> map() {
        EntityContainer entityContainer = configuration.getEntityContainer();
        EntityReference entityReference = entityContainer.resolve(clazz);

        Map<Object, T> mainEntityMap = new HashMap<>();
        Set<String> recursionStack = new HashSet<>();
        String relationshipKey = "";

        for (Map<String, Object> row : data) {
            Object mainEntityId = extractMainEntityId(row, entityReference);
            if (!mainEntityMap.containsKey(mainEntityId)) {
                T instance = ReflectionUtil.newInstance(clazz);
                mapColumns(row, instance, entityReference, recursionStack, relationshipKey);
                mainEntityMap.put(mainEntityId, instance);
            }
        }
        return mainEntityMap.values().stream().toList();
    }

    private void mapColumns(Map<String, Object> row, T instance, EntityReference entityReference, Set<String> recursionStack, String relationshipKey) {
        entityReference.getTableMetadata().getAllColumns()
                .forEach(column -> {
                    String columnName = QueryBuilderUtil.getScopedNameFromMapping(entityReference.getTableMetadata().getTableName(), column.getColumnName());
                    boolean isManyToOne = column.getAnnotations().stream().anyMatch(annotation -> annotation instanceof ManyToOne);
                    boolean isOneToMany = column.getAnnotations().stream().anyMatch(annotation -> annotation instanceof OneToMany);
                    boolean isManyToMany = column.getAnnotations().stream().anyMatch(annotation -> annotation instanceof ManyToMany);

                    if (isManyToOne) {
                        mapManyToOneReferences(row, instance, column, entityReference, recursionStack);
                    } else if (isOneToMany) {
                        mapOneToManyReferences(row, instance, column, entityReference, recursionStack);
                    } else if (isManyToMany) {
                        mapManyToManyReferences(row, instance, column, entityReference, recursionStack);
                    } else {
                        if (!row.containsKey(columnName)) {
                            throw new InvalidMappingException(String.format("Column %s is not present in the result set", column.getColumnName()));
                        }

                        Object value = row.get(columnName);
                        ReflectionUtil.setField(instance, instance.getClass(), column.getName(), value);
                    }
                });
    }

    private void mapManyToOneReferences(Map<String, Object> row, T instance, ColumnMetadata column, EntityReference entityReference, Set<String> recursionStack) {
        entityReference.getManyToOneReferences().forEach(reference -> {
            String relationshipKey = instance.getClass().getName() + "_" + reference.getEntityClass().getName();

            boolean found = recursionStack.stream().anyMatch(key -> {
                String[] splittedKey = QueryBuilderUtil.splitRelationshipKey(key, "_");
                return reference.getEntityClass().getName().equals(splittedKey[0]) && instance.getClass().getName().equals(splittedKey[1]);
            });

            if (!recursionStack.add(relationshipKey) || found) {
                return;
            }

            Object referencedInstance = mapEntityInstance(row, column, reference, recursionStack, relationshipKey);
            ReflectionUtil.setField(instance, instance.getClass(), column.getName(), referencedInstance);
        });
    }

    private void mapOneToManyReferences(Map<String, Object> row, T instance, ColumnMetadata column, EntityReference entityReference, Set<String> recursionStack) {
        entityReference.getOneToManyReferences().forEach(reference -> {
            String relationshipKey = instance.getClass().getName() + "_" + reference.getEntityClass().getName();

            boolean found = recursionStack.stream().anyMatch(key -> {
                String[] splittedKey = QueryBuilderUtil.splitRelationshipKey(key, "_");
                return reference.getEntityClass().getName().equals(splittedKey[0]) && instance.getClass().getName().equals(splittedKey[1]);
            });

            if (!recursionStack.add(relationshipKey) || found) {
                return;
            }

            List<Object> referencedInstances = mapEntityInstances(column, reference, recursionStack, relationshipKey);
            ReflectionUtil.setField(instance, instance.getClass(), column.getName(), referencedInstances);
        });
    }

    private void mapManyToManyReferences(Map<String, Object> row, T instance, ColumnMetadata column, EntityReference entityReference, Set<String> recursionStack) {
        entityReference.getManyToManyReferences().forEach(reference -> {
            String relationshipKey = instance.getClass().getName() + "_" + reference.getEntityClass().getName();

            boolean found = recursionStack.stream().anyMatch(key -> {
                String[] splittedKey = QueryBuilderUtil.splitRelationshipKey(key, "_");
                return reference.getEntityClass().getName().equals(splittedKey[0]) && instance.getClass().getName().equals(splittedKey[1]);
            });

            if (!recursionStack.add(relationshipKey) || found) {
                return;
            }

            List<Object> referencedInstances = mapEntityInstances(column, reference, recursionStack, relationshipKey);
            List<Object> notNull = referencedInstances.stream().filter(Objects::nonNull).collect(Collectors.toList());
            ReflectionUtil.setField(instance, instance.getClass(), column.getName(), notNull);
        });
    }

    private Object mapEntityInstance(Map<String, Object> row, ColumnMetadata column, EntityReference entityReference, Set<String> recursionStack, String relationshipKey) {
        String startsWith = entityReference.getTableMetadata().getTableName() + ".";
        Set<String> subRow = row.keySet()
                .stream()
                .filter(entry -> entry.startsWith(startsWith))
                .collect(Collectors.toSet());
        List<Object> subRowValues = subRow.stream().map(row::get).toList();
        if (subRowValues.stream().allMatch(Objects::isNull)) {
            return null;
        }

        T referencedInstance = (T) ReflectionUtil.newInstance(entityReference.getEntityClass());

        mapColumns(row, referencedInstance, entityReference, recursionStack, relationshipKey);
        mapManyToOneReferences(row, referencedInstance, column, entityReference, recursionStack);
        mapOneToManyReferences(row, referencedInstance, column, entityReference, recursionStack);
        mapManyToManyReferences(row, referencedInstance, column, entityReference, recursionStack);

        return referencedInstance;
    }

    private List<Object> mapEntityInstances(ColumnMetadata column, EntityReference entityReference, Set<String> recursionStack, String relationshipKey) {
        return data
                .stream()
                .map(subRow -> mapEntityInstance(subRow, column, entityReference, recursionStack, relationshipKey))
                .toList();
    }

    private Object extractMainEntityId(Map<String, Object> row, EntityReference entityReference) {
        // Assuming the main entity has a single ID column for simplicity
        ColumnMetadata idColumn = entityReference.getTableMetadata().getIdColumns().get(0);
        String columnName = QueryBuilderUtil.getScopedNameFromMapping(entityReference.getTableMetadata().getTableName(), idColumn.getColumnName());

        // Extract the ID value from the row
        return row.get(columnName);
    }
}
