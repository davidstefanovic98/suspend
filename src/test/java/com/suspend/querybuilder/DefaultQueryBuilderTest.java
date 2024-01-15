package com.suspend.querybuilder;

import com.suspend.annotation.Column;
import com.suspend.annotation.Id;
import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultQueryBuilderTest {

    public static final class TestModel {
        @Id
        private Integer id;
        @Column
        private String name;
        @Column
        private int age;
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
        assertEquals("SELECT * FROM TestModel", builder.select().build());
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
}