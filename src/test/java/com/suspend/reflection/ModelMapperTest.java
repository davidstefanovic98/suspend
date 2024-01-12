package com.suspend.reflection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelMapperTest {

    public static final class TestModel {
        private String name;
        private int age;
    }

    @Test
    void testMap() {
        TableMapper mapper = new TableMapper();

        TableMetadata metadata = mapper.getMetadata(TestModel.class);
        assertEquals("TestModel", metadata.tableName());
        assertEquals(2, metadata.columns().size());
        assertEquals("name", metadata.columns().get(0).name());
        assertEquals(String.class, metadata.columns().get(0).type());
        assertEquals("age", metadata.columns().get(1).name());
        assertEquals(int.class, metadata.columns().get(1).type());
    }
}