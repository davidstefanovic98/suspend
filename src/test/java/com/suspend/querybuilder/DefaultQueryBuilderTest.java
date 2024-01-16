package com.suspend.querybuilder;

import com.suspend.annotation.*;
import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultQueryBuilderTest {

    @Table(name = "test_model")
    public static final class TestModel {
        @Id
        private Integer id;
        @Column
        private String name;
        @Column
        private int age;
        @ManyToOne
        @JoinColumn(name = "test_model_fk", referencedColumnName = "id")
        private TestModel2 testModel;
    }

    @Table(name = "test_model_2")
    public static final class TestModel2 {
        @Id
        private Integer id;

        @Column
        private String name;
    }

    TestModel testEntity;
    @BeforeEach
    void setUp() {
        testEntity = new TestModel();
        testEntity.id = 1;
        testEntity.name = "John";
        testEntity.age = 30;
    }

    @Test
    void testSelect() {
        TableMetadata metadata = new TableMapper().getMetadata(TestModel.class);
        QueryBuilder builder = new DefaultQueryBuilder(metadata);
        assertEquals("SELECT * FROM TestModel", builder.select().join().build());
    }

    @Test
    void testInsert() {
        TableMetadata metadata = new TableMapper().getMetadata(testEntity);
        QueryBuilder builder = new DefaultQueryBuilder(metadata);
        assertEquals("INSERT INTO TestModel (name, age)  VALUES ('John', 30)", builder.insert().build());
    }

    @Test
    void testUpdate() {
        TableMapper mapper = new TableMapper();
        TableMetadata metadata = mapper.getMetadata(testEntity);
        QueryBuilder builder = new DefaultQueryBuilder(metadata);
        builder.update();
        assertEquals("UPDATE TestModel SET name = 'John', age = 30 WHERE id = 1", builder.build());
    }

    @Test
    void testJoin() {
        TableMapper mapper = new TableMapper();
        TableMetadata metadata = mapper.getMetadata(testEntity);
        QueryBuilder builder = new DefaultQueryBuilder(metadata);

        builder.join();

        assertEquals("Whatever", builder.build());

    }
}