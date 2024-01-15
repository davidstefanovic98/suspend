package com.suspend.converter;

import java.math.BigInteger;

public class AttributeConverter {

    public static Object convert(Object value, Class<?> type) {
        if (type == Integer.class || type == int.class || type == BigInteger.class) {
            return Integer.parseInt(value.toString());
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(value.toString());
        } else if (type == Double.class || type == double.class) {
            return Double.parseDouble(value.toString());
        } else if (type == Float.class || type == float.class) {
            return Float.parseFloat(value.toString());
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value.toString());
        } else if (type == String.class) {
            return value.toString();
        } else {
            throw new RuntimeException("Unsupported type: " + type);
        }
    }
}
