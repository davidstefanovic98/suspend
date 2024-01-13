package com.suspend.querybuilder;

import com.suspend.reflection.ColumnMetadata;
import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;
import com.suspend.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Mapper<T> {

    private final TableMetadata tableMetadata;
    private final List<Map<String, Object>> data;
    private final Class<T> clazz;

    public Mapper(Class<T> clazz, List<Map<String, Object>> data) {
        this.tableMetadata = new TableMapper().getMetadata(clazz);
        this.data = data;
        this.clazz = clazz;
    }

    public List<T> map() {
        List<T> result = new ArrayList<>();

        data.forEach(row -> {
            T instance = ReflectionUtil.newInstance(clazz);
            tableMetadata.getColumns().forEach(column -> {
                Object value = row.get(column.getName());
                try {
                    Field field = clazz.getDeclaredField(column.getName());
                    field.setAccessible(true);
                    field.set(instance, value);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            result.add(instance);
        });
        return result;
    }


    private void assignValue(TableMetadata tableMetadata, String columnName, Object value) {
        ColumnMetadata field = tableMetadata.getColumns().stream()
            .filter(column -> column.getName().equals(columnName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Column not found"));
        field.setValue(value);
    }
}
