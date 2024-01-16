package com.suspend.mapper;

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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Mapper<T> {

    private final List<Map<String, Object>> data;
    private final Class<T> clazz;
    private final Logger logger = LoggerFactory.getLogger(Mapper.class);
    private final Configuration configuration;
    private final Set<Object> mappedInstances = new HashSet<>();

    public Mapper(Class<T> clazz, List<Map<String, Object>> data) {
        this.data = data;
        this.clazz = clazz;
        configuration = Configuration.getInstance();
    }

    public List<T> map() {
        EntityContainer entityContainer = configuration.getEntityContainer();
        EntityReference entityReference = entityContainer.resolve(clazz);
        return data.stream()
                .map(row -> {
                    T instance = (T) ReflectionUtil.newInstance(entityReference.getEntityClass());
                    mapColumns(row, instance, entityReference);
                    return instance;
                })
                .toList();
    }

    private void mapColumns(Map<String, Object> row, T instance, EntityReference entityReference) {
        entityReference.getTableMetadata().getAllColumns()
                .forEach(column -> {
                    String columnName = QueryBuilderUtil.getScopedNameFromMapping(entityReference.getTableMetadata().getTableName(), column.getColumnName());
                    boolean isManyToOne = column.getAnnotations().stream().anyMatch(annotation -> annotation instanceof ManyToOne);
                    boolean isOneToMany = column.getAnnotations().stream().anyMatch(annotation -> annotation instanceof OneToMany);

                    if (isManyToOne) {
                        mapManyToOneReferences(row, instance, column, entityReference);
                    } else if (isOneToMany) {
                        mapOneToManyReferences(row, instance, column, entityReference);
                    } else {
                        if (!row.containsKey(columnName)) {
                            throw new InvalidMappingException(String.format("Column %s is not present in the result set", column.getColumnName()));
                        }

                        Object value = row.get(columnName);
                        ReflectionUtil.setField(instance, instance.getClass(), column.getName(), value);
                    }
                });
    }

    private void mapManyToOneReferences(Map<String, Object> row, T instance, ColumnMetadata column, EntityReference entityReference) {
        entityReference.getManyToOneReferences().forEach(reference -> {
            if (mappedInstances.contains(instance)) {
                return;
            }
            Object referencedInstance = mapEntityInstance(row, column, reference);
            ReflectionUtil.setField(instance, clazz, column.getName(), referencedInstance);
        });
    }

    private void mapOneToManyReferences(Map<String, Object> row, T instance, ColumnMetadata column, EntityReference entityReference) {
        entityReference.getOneToManyReferences().forEach(reference -> {
            if (mappedInstances.contains(instance)) {
                return;
            }
            List<Object> referencedInstances = mapEntityInstances(row, column, reference);
            ReflectionUtil.setField(instance, clazz, column.getName(), referencedInstances);
        });
    }

    private Object mapEntityInstance(Map<String, Object> row, ColumnMetadata column, EntityReference entityReference) {
        T referencedInstance = (T) ReflectionUtil.newInstance(entityReference.getEntityClass());
        mappedInstances.add(referencedInstance);
        mapColumns(row, referencedInstance, entityReference);
        mapManyToOneReferences(row, referencedInstance, column, entityReference);
        mapOneToManyReferences(row, referencedInstance, column, entityReference);
        return referencedInstance;
    }

    private List<Object> mapEntityInstances(Map<String, Object> row, ColumnMetadata column, EntityReference entityReference) {
        return data
                .stream()
                .map(subRow -> mapEntityInstance(subRow, column, entityReference))
                .toList();
    }
}
