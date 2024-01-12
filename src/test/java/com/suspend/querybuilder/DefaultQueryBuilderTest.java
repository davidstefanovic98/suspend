package com.suspend.querybuilder;

import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultQueryBuilderTest {

    public static final class TestModel {
        private String name;
        private int age;
    }

    @Test
    void testSelect() {
        TableMapper mapper = new TableMapper();
        TableMetadata metadata = mapper.getMetadata(TestModel.class);
        QueryBuilder builder = new DefaultQueryBuilder(metadata);
        builder.select();
        assertEquals("SELECT name, age FROM TestModel", builder.build());
    }

    @Test
    void testInsert() {
        TableMapper mapper = new TableMapper();
        TableMetadata metadata = mapper.getMetadata(TestModel.class);
        QueryBuilder builder = new DefaultQueryBuilder(metadata);
        builder.insert();
        assertEquals("INSERT INTO TestModel (name, age)  VALUES (?, ?)", builder.build());
    }

    @Test
    void testUpdate() {
        TableMapper mapper = new TableMapper();
        TableMetadata metadata = mapper.getMetadata(TestModel.class);
        QueryBuilder builder = new DefaultQueryBuilder(metadata);
        builder.update();
        assertEquals("UPDATE TestModel SET name = ?, age = ?", builder.build());
    }
}