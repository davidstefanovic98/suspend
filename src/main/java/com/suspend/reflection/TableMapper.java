package com.suspend.reflection;

import com.suspend.annotation.*;
import com.suspend.exception.AnnotationMissingException;
import com.suspend.querybuilder.Join;
import com.suspend.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TableMapper {

    private final Logger logger = LoggerFactory.getLogger(TableMapper.class);

    public TableMetadata getMetadata(Class<?> clazz) {
        Object instance = ReflectionUtil.newInstance(clazz);
        return new TableMetadata(
                ReflectionUtil.getTableName(clazz),
                clazz.getSimpleName(),
                getColumns(Column.class, instance),
                getColumns(Id.class, instance),
                getOneToManyColumns(instance),
                getManyToOneColumns(instance),
                clazz);
    }

    public <T> TableMetadata getMetadata(T instance) {
        return new TableMetadata(
                ReflectionUtil.getTableName(instance.getClass()),
                instance.getClass().getSimpleName(),
                getColumns(Column.class, instance),
                getColumns(Id.class, instance),
                getOneToManyColumns(instance),
                getManyToOneColumns(instance),
                instance.getClass());
    }

    private List<ColumnMetadata> getColumns(Class<? extends Annotation> annotationClass, Object instance) {
        Class<?> clazz = instance.getClass();

        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        List<? extends Annotation> annotations = new ArrayList<>(Arrays.asList(field.getAnnotations()));
                        Object value = field.get(instance);
                        String columnName = ReflectionUtil.getColumnName(field);
                        return new ColumnMetadata(field.getType(), field.getName(), value, columnName, annotations);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    private List<ColumnMetadata> getManyToOneColumns(Object instance) {
        Class<?> clazz = instance.getClass();
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ManyToOne.class))
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        List<? extends Annotation> annotations = new ArrayList<>(Arrays.asList(field.getAnnotations()));
                        Object value = field.get(instance);
                        String columnName = ReflectionUtil.getColumnName(field);

                        if (field.isAnnotationPresent(JoinColumn.class)) {
                            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                            if (joinColumn.name().isEmpty())
                                return new ColumnMetadata(field.getType(), field.getName(), value, columnName, annotations);
                            return new ColumnMetadata(field.getType(), field.getName(), value, joinColumn.name(), annotations);
                        }
                        return new ColumnMetadata(field.getType(), field.getName(), value, columnName, annotations);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    private List<ColumnMetadata> getOneToManyColumns(Object instance) {
        Class<?> clazz = instance.getClass();
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        List<? extends Annotation> annotations = new ArrayList<>(Arrays.asList(field.getAnnotations()));
                        Object value = field.get(instance);
                        String columnName = ReflectionUtil.getColumnName(field);
                        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                        Class<?> type = ReflectionUtil.getGenericTypeFromField(field);

                        Optional<Field> matchingManyToOneColumn =  Arrays.stream(type.getDeclaredFields())
                                .filter(f -> f.isAnnotationPresent(ManyToOne.class))
                                .filter(f -> f.getName().equals(oneToMany.mappedBy()))
                                .findFirst();
                        matchingManyToOneColumn.filter(f -> !f.isAnnotationPresent(JoinColumn.class))
                                .ifPresent(f -> {
                                    throw new AnnotationMissingException(String.format("The @JoinColumn annotation is missing from the field '%s' in the class '%s'.", f.getName(), type.getName()));
                                });

                        if (matchingManyToOneColumn.isPresent()) {
                            JoinColumn column = matchingManyToOneColumn.get().getAnnotation(JoinColumn.class);
//                            if (field.isAnnotationPresent(JoinColumn.class)) {
                                //TODO: Implement bidirectional relationship
//                                JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
//                                if (joinColumn == null) {
//
//                                }
//                                if (joinColumn.name().isEmpty())
//                                    return new ColumnMetadata(ReflectionUtil.getGenericTypeFromField(field), field.getName(), value, columnName, annotations);
//                                return new ColumnMetadata(ReflectionUtil.getGenericTypeFromField(field), field.getName(), value, joinColumn.name(), annotations);
//                            } else {
                                if (oneToMany.mappedBy().isEmpty())
                                    throw new AnnotationMissingException(String.format("The @OneToMany mappedBy attribute not set for field %s in %s", field.getName(), clazz.getName()));
                                return new ColumnMetadata(ReflectionUtil.getGenericTypeFromField(field), field.getName(), value, column.referencedColumnName(), annotations);
                            } else {
                             throw new AnnotationMissingException(String.format("The @OneToMany mappedBy attribute not properly set for field %s in %s", field.getName(), clazz.getName()));
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
