package com.suspend.reflection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TableMapper {

    public TableMetadata getMetadata(Class<?> clazz) {
        return new TableMetadata(clazz.getSimpleName(), getColumns(clazz));
    }

    private List<ColumnMetadata> getColumns(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .map(field -> new ColumnMetadata(field.getType(), field.getName()))
            .collect(Collectors.toList());
    }
}
