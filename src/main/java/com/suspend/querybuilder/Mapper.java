package com.suspend.querybuilder;

import com.suspend.exception.InvalidMappingException;
import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;
import com.suspend.util.ReflectionUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
        return data.stream()
                .map(row -> {
                    T instance = ReflectionUtil.newInstance(clazz);
                    Stream.concat(tableMetadata.getColumns().stream(), tableMetadata.getIdColumns().stream())
                            .forEach(column -> {
                                Object value = row.get(column.getColumnName());
                                if (!row.containsKey(column.getColumnName())) {
                                    throw new InvalidMappingException(String.format("The column '%s' is not present in the result set.", column.getColumnName()));
                                }
                                ReflectionUtil.setField(instance, clazz, column.getName(), value);
                            });
                    return instance;
                })
                .toList();
    }
}
