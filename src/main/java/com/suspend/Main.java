package com.suspend;

import com.suspend.querybuilder.DefaultQueryBuilder;
import com.suspend.repository.Repository;
import com.suspend.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
//        Set<Class<? extends Repository>> classes = ReflectionUtil.getClasses(Repository.class);
//        classes.forEach(clazz -> {
//            Type[] types = clazz.getGenericInterfaces();
//            for (Type type : types) {
//                Arrays.stream(type.getClass().getDeclaredFields()).forEach(
//                        field -> {
//                            logger.info("Field: {}", field.getGenericType());
//                        }
//                );
//            }
//            System.out.println(clazz.getName());
//        });
    }
}