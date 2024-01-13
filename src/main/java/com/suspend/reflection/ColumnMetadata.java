package com.suspend.reflection;

import java.util.Objects;

//@Todo: Implement this class
public final class ColumnMetadata {

    private Class<?> type;
    private String name;
    private Object value;

    public ColumnMetadata(Class<?> type, String name, Object value) {
        this.type = type;
        this.name = name;
        this.value = value;
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

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
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
                "value=" + value + ']';
    }

}
