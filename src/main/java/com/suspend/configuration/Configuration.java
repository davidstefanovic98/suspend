package com.suspend.configuration;

import com.suspend.annotation.*;
import com.suspend.connection.ConnectionManager;
import com.suspend.entitymanager.EntityContainer;
import com.suspend.entitymanager.EntityManager;
import com.suspend.entitymanager.EntityReference;
import com.suspend.exception.AnnotationMissingException;
import com.suspend.repository.Repository;
import com.suspend.repository.RepositoryFactory;
import com.suspend.util.ReflectionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class Configuration {

    private final Properties properties;
    private final Map<Class<?>, Class<?>> repositoryMap;
    private String repositoryPackageName;
    private String entityPackageName;
    private static Configuration instance = null;
    private RepositoryFactory repositoryFactory;
    private final List<EntityReference> entityReferences;
    private final EntityContainer entityContainer;

    public Configuration() {
        this.properties = getApplicationProperties();
        this.repositoryMap = new HashMap<>();
        // Default package
        this.repositoryPackageName = "";
        entityContainer = new EntityContainer();
        entityReferences = new ArrayList<>();
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public void addAnnotatedClass(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> entityClasses = ReflectionUtil.getClassesAnnotatedBy(getEntityPackageName(), Table.class);
        Set<Class<? extends Repository>> classes = ReflectionUtil.getClasses(getRepositoryPackageName(), Repository.class);

        entityClasses.forEach(entityClass -> {
            EntityReference entityReference = entityContainer.resolve(entityClass);
            for (Field field : entityClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(ManyToOne.class)) {
                        EntityReference reference = entityContainer.resolve(field.getType());
                        reference.setOwner(field.isAnnotationPresent(JoinColumn.class));

                        reference.setParent(entityReference);
                        entityReference
                                .getManyToOneReferences()
                                .add(entityContainer.initialize(reference));
                    }

                    if (field.isAnnotationPresent(OneToMany.class)) {
                        Class<?> clazz;
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            clazz = ReflectionUtil.getGenericTypeFromField(field);
                        } else if (field.getType().isArray()) {
                            clazz = field.getType().getComponentType();
                        } else {
                            clazz = field.getType();
                        }
                        EntityReference reference = entityContainer.resolve(clazz);
                        reference.setOwner(field.isAnnotationPresent(JoinColumn.class));
                        reference.setParent(entityReference);
                        entityReference
                                .getOneToManyReferences()
                                .add(entityContainer.initialize(reference));
                    }

                    if (field.isAnnotationPresent(ManyToMany.class)) {
                        Class<?> clazz;
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            clazz = ReflectionUtil.getGenericTypeFromField(field);
                        } else if (field.getType().isArray()) {
                            clazz = field.getType().getComponentType();
                        } else {
                            clazz = field.getType();
                        }

                        EntityReference reference = entityContainer.resolve(clazz);
                        reference.setOwner(field.isAnnotationPresent(JoinTable.class));
                        reference.setParent(entityReference);
                        entityReference
                                .getManyToManyReferences()
                                .add(entityContainer.initialize(reference));
                    }
            }
            if (entityClass.isAnnotationPresent(annotationClass)) {
                Class<?> repositoryClass = classes.stream()
                        .filter(clazz -> !clazz.isAnnotationPresent(Exclude.class) && ReflectionUtil.getGenericTypeFromInterface(clazz).equals(entityClass))
                        .findFirst()
                        .orElse(null);
                repositoryMap.put(entityClass, repositoryClass);
            } else {
                throw new AnnotationMissingException("Entity " + entityClass.getName() + " is not annotated with " + annotationClass.getName());
            }
        });

    }

    public EntityManager buildEntityManager() {
        return new EntityManager();
    }

    public List<EntityReference> getEntityReferences() {
        return entityReferences;
    }

    public EntityContainer getEntityContainer() {
        return entityContainer;
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

    public String getEntityPackageName() {
        return entityPackageName;
    }

    public void setEntityPackageName(String entityPackageName) {
        this.entityPackageName = entityPackageName;
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
