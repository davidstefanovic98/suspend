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
        assertEquals("TestModel", metadata.getTableName());
        assertEquals(2, metadata.getColumns().size());
        assertEquals("name", metadata.getColumns().get(0).getName());
        assertEquals(String.class, metadata.getColumns().get(0).getType());
        assertEquals("age", metadata.getColumns().get(1).getName());
        assertEquals(int.class, metadata.getColumns().get(1).getType());
    }
}