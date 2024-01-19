package com.suspend.reflection;

import com.suspend.annotation.*;
import com.suspend.exception.AnnotationMissingException;
import com.suspend.exception.InvalidAnnotationException;
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
                getManyToManyColumns(instance),
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
                getManyToManyColumns(instance),
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

    //TODO: Implement that OneToMany be owner of association
    private List<ColumnMetadata> getOneToManyColumns(Object instance) {
        Class<?> clazz = instance.getClass();
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        List<? extends Annotation> annotations = new ArrayList<>(Arrays.asList(field.getAnnotations()));
                        Object value = field.get(instance);
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


    private List<ColumnMetadata> getManyToManyColumns(Object instance) {
        Class<?> clazz = instance.getClass();
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ManyToMany.class))
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        List<? extends Annotation> annotations = new ArrayList<>(Arrays.asList(field.getAnnotations()));
                        Object value = field.get(instance);
                        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                        JoinTable joinTable = field.getAnnotation(JoinTable.class);

                        if (manyToMany != null) {
                            Class<?> type = ReflectionUtil.getGenericTypeFromField(field);
                            Optional<Field> matchingManyToManyColumn = Arrays.stream(type.getDeclaredFields())
                                    .filter(f -> f.isAnnotationPresent(ManyToMany.class))
                                    .findFirst();
                            if (matchingManyToManyColumn.isEmpty()) {
                                throw new AnnotationMissingException(String.format("The @ManyToMany is missing in class %s", type.getName()));
                            } else {
                                JoinTable matchingJoinTable = matchingManyToManyColumn
                                        .map(f -> f.getAnnotation(JoinTable.class))
                                        .orElse(null);

                                ManyToMany matchingManyToMany = matchingManyToManyColumn
                                        .map(f -> f.getAnnotation(ManyToMany.class))
                                        .orElse(null);

                                if (matchingJoinTable == null && matchingManyToMany.mappedBy().isEmpty()) {
                                    throw new AnnotationMissingException(String.format("The @ManyToMany not properly set in class %s", type.getName()));
                                } else if (matchingJoinTable == null && joinTable == null) {
                                    throw new AnnotationMissingException("The @JoinTable is missing in the relationship");
                                } else if (matchingJoinTable != null && joinTable != null) {
                                    throw new InvalidAnnotationException("Two @JoinTable found in the relationship");
                                } else if (matchingManyToMany.mappedBy().isEmpty() && manyToMany.mappedBy().isEmpty()) {
                                    throw new AnnotationMissingException("The mappedBy attribute in @ManyToMany is missing");
                                } else {
                                    JoinColumn joinColumn = joinTable == null ? null : joinTable.joinColumns()[0];
                                    JoinColumn matchingJoinColumn = matchingJoinTable == null ? null : matchingJoinTable.joinColumns()[0];

                                    if (joinColumn == null && matchingJoinColumn == null) {
                                        throw new AnnotationMissingException("The @JoinColumn is missing in the relationship");
                                    } else if (joinColumn != null && matchingJoinColumn != null) {
                                        throw new InvalidAnnotationException("Two @JoinColumn found in the relationship");
                                    } else {
                                        return new ColumnMetadata(ReflectionUtil.getGenericTypeFromField(field), field.getName(), value, matchingJoinColumn != null ? matchingJoinColumn.name() : joinColumn.name(), annotations);
                                    }
                                }
                            }
                        } else {
                            throw new AnnotationMissingException(String.format("The @ManyToMany is missing from %s in class %s", field.getName(), clazz.getName()));
                        }

                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
