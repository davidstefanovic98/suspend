package com.suspend.util;

import com.suspend.annotation.Column;
import com.suspend.annotation.Table;
import com.suspend.exception.IncorrectTypeException;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

public class ReflectionUtil {

    private ReflectionUtil() {
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    // @Note: This can be done differently, but for error handling purposes, I think this is the best way.
    public static <T> void setField(T instance, Class<?> clazz, String fieldName, Object value) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(String.format("The field '%s' does not exist.", fieldName));
        }

        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new IncorrectTypeException(
                    String.format(
                            "The value of '%s' is not of the correct type. Expected: '%s', got '%s'",
                            fieldName,
                            field.getType().getName(),
                            value.getClass().getName()));
        }
    }

    public static String getColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::name)
                .filter(name -> !name.isEmpty())
                .orElse(field.getName());
    }

    public static String getTableName(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(Table.class))
                .map(Table::name)
                .filter(name -> !name.isEmpty())
                .orElse(clazz.getSimpleName());
    }

    public static void setField(Object instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Set<Class<? extends T>> getClasses(String packageName, Class<T> clazz) {
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes);
        return reflections.getSubTypesOf(clazz);
    }

    public static <T> Class<T> getGenericTypeFromInterface(Class<T> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                for (Type genericType : genericTypes) {
                    return (Class<T>) genericType;
                }
            }
        }
        return clazz;
    }

    public static <T> Class<T> getGenericTypeFromSuperclass(Class<T> clazz) {
        Type genericInterface = clazz.getGenericSuperclass();
            if (genericInterface instanceof ParameterizedType) {
                Type genericType = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                return (Class<T>) genericType;
            }
            return clazz;
        }
    }
