package com.suspend.configuration;

import com.suspend.annotation.Exclude;
import com.suspend.connection.ConnectionManager;
import com.suspend.exception.AnnotationMissingException;
import com.suspend.entitymanager.EntityManager;
import com.suspend.repository.Repository;
import com.suspend.repository.RepositoryFactory;
import com.suspend.util.ReflectionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.*;

public class Configuration {

    private final Properties properties;
    private final Map<Class<?>, Class<?>> repositoryMap;
    private String repositoryPackageName;
    private static Configuration instance = null;
    private RepositoryFactory repositoryFactory;

    public Configuration() {
        this.properties = getApplicationProperties();
        this.repositoryMap = new HashMap<>();
        // Default package
        this.repositoryPackageName = "";
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public void addAnnotatedClass(Class<? extends Annotation> annotationClass) {
        List<Class<?>> entityClasses = new ArrayList<>();
        Set<Class<? extends Repository>> classes = ReflectionUtil.getClasses(getRepositoryPackageName(), Repository.class);
        classes.stream().filter(c -> !c.isAnnotationPresent(Exclude.class)).forEach(clazz -> {
            Class<?> entityClass = ReflectionUtil.getGenericTypeFromInterface(clazz);
            entityClasses.add(entityClass);
        });
        entityClasses.forEach(entityClass -> {
            if (entityClass.isAnnotationPresent(annotationClass)) {
                Class<?> repositoryClass = classes.stream()
                        .filter(clazz -> !clazz.isAnnotationPresent(Exclude.class) && ReflectionUtil.getGenericTypeFromInterface(clazz).equals(entityClass))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Repository not found for entity " + entityClass.getName()));
                repositoryMap.put(entityClass, repositoryClass);
            } else {
                throw new AnnotationMissingException("Entity " + entityClass.getName() + " is not annotated with " + annotationClass.getName());
            }
        });
    }

    public EntityManager buildEntityManager() {
        return new EntityManager();
    }

    public RepositoryFactory getRepositoryFactory() {
        return new RepositoryFactory(this);
    }

    public Properties getProperties() {
        return properties;
    }

    public String getRepositoryPackageName() {
        return repositoryPackageName;
    }

    public void setRepositoryPackageName(String repositoryPackageName) {
        this.repositoryPackageName = repositoryPackageName;
    }

    public Map<Class<?>, Class<?>> getRepositoryMap() {
        return repositoryMap;
    }

    public Properties getApplicationProperties() {
        Properties properties = new Properties();

        try(InputStream inputStream = ConnectionManager.class.getResourceAsStream("/application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
