package com.suspend.reflection;

import com.suspend.annotation.Column;
import com.suspend.annotation.Id;
import com.suspend.annotation.ManyToOne;
import com.suspend.annotation.OneToMany;
import com.suspend.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class TableMapper {

    private final Logger logger = LoggerFactory.getLogger(TableMapper.class);

    public TableMetadata getMetadata(Class<?> clazz) {
        Object instance = ReflectionUtil.newInstance(clazz);
        return new TableMetadata(
                ReflectionUtil.getTableName(clazz),
                clazz.getSimpleName(),
                getColumns(Column.class, instance),
                getColumns(Id.class, instance),
                getColumns(OneToMany.class, instance),
                getColumns(ManyToOne.class, instance));
    }

    public <T> TableMetadata getMetadata(T instance) {
        return new TableMetadata(
                ReflectionUtil.getTableName(instance.getClass()),
                instance.getClass().getSimpleName(),
                getColumns(Column.class, instance),
                getColumns(Id.class, instance),
                getColumns(OneToMany.class, instance),
                getColumns(ManyToOne.class, instance));
    }

    private List<ColumnMetadata> getColumns(Class<? extends Annotation> annotationClass, Object instance) {
        Class<?> clazz = instance.getClass();
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(instance);
                        String columnName = ReflectionUtil.getColumnName(field);
                        return new ColumnMetadata(field.getType(), field.getName(), value, columnName);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
