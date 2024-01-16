package com.suspend.reflection;

import com.suspend.annotation.ManyToOne;
import com.suspend.annotation.OneToMany;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

//@Todo: Implement this class
public final class ColumnMetadata {

    private final Class<?> type;
    private final String name;
    private final Object value;
    private final String columnName;
    private List<? extends Annotation> annotations;

    public ColumnMetadata(Class<?> type, String name, Object value, String columnName, List<? extends Annotation> annotations) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.columnName = columnName;
        this.annotations = annotations;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getColumnName() {
        return columnName;
    }

    public List<? extends Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ColumnMetadata) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, value);
    }

    @Override
    public String toString() {
        return "ColumnMetadata[" +
                "type=" + type + ", " +
                "name=" + name + ", " +
                "value=" + value + ", " +
                "columnName=" + columnName + ']';
    }

    public boolean isOneToMany() {
        return annotations.stream().anyMatch(annotation -> annotation instanceof OneToMany);
    }

    public boolean isManyToOne() {
        return annotations.stream().anyMatch(annotation -> annotation instanceof ManyToOne);
    }
}
