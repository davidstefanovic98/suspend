# Suspend
___

## Description
___
A light ORM framework for Java inspired by Spring Data JPA and Hibernate.
This project showcase the use of Java Reflection API and Annotation Processing API.

## Features
___
- [x] Annotation based entity mapping
- [x] Repository interface for CRUD operations (inspired by Spring Data JPA)
- [ ] Annotation based query generation
- [ ] Namespace resolution for query generation
- [ ] Interface for interacting with persistence context (EntityManager)

## Usage
___
### Table Mapping
```java
@Table(name = "test_model")
public class TestModel {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;
    
    @ManyToOne
    @JoinColumn(name = "test_model_2_id")
    private TestModel2 testModel2;
}
```
@Table annotation is used to map the class to a table in the database. The name attribute is used to specify the table name. If the name attribute is not specified, the table name will be the same as the class name.
@Table annotation is required for all entity classes.

@Id annotation is used to specify the primary key of the table. The field annotated with @Id must be of type Long, Integer or String. The field annotated with @Id must be unique. @Id annotation is required for all entity classes.
If the field name is different from the column name, the column name can be specified using @Column annotation with the name attribute.

@Column annotation is used to specify the column name. If the name attribute is not specified, the column name will be the same as the field name.
@Column annotation is mandatory for all fields except for relationship fields.

@ManyToOne annotation is used to specify a many-to-one relationship. @JoinColumn annotation is used to specify the join column name. If the name attribute is not specified, the join column name will be the same as the field name.

@OneToMany annotation is used to specify a one-to-many relationship. In @OneToMany annotation, the mappedBy attribute is used to specify the field name of the many-to-one relationship in the target entity class and is mandatory.

### Repository
```java

public interface TestModelRepository extends Repository<TestModel, Integer> {

}
```

TestModelRepository is an interface that extends Repository interface. The first generic type is the entity class and the second generic type is the type of the primary key. The repository interface is used to perform CRUD operations on the entity class.
For now, it only provides the basic CRUD operations. Annotation based query and namespace resolution for query generation will be added in the future.

### Configuration
```java
@Grain
public class Config {

    @Grain
    public Configuration configuration(GrainInjector injector) {
        Configuration configuration = Configuration.getInstance();
        configuration.setRepositoryPackageName("com.suspend.repository");
        configuration.setEntityPackageName("com.suspend.entity");
        configuration.addAnnotatedClass(Table.class);
        configuration.getRepositoryFactory().getRepositories().forEach(injector::inject);
        return configuration;
    }
}
```
Configuration class is used to configure the way Suspend works. Repository package name and entity package name must be set in order for Suspend to scan for entities and repositories.
Only entities with @Table annotation will be scanned and only repositories that extend Repository interface are scanned.
Method addAnnotatedClass is used to scan the whole project for entities. This method used to separate entities from non-entities classes that are in entity package.
The configuration as it is now is not ideal, since is solely based on working with dependency injection frameworks ([Spring](https://github.com/spring-projects/spring-framework) or in this case [Grain](https://github.com/7aske/grain)). In the future, the configuration will be changed to be more flexible.

#### More features will be added in the future. Stay tuned!