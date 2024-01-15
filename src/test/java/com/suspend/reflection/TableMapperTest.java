package com.suspend.reflection;

import com.suspend.annotation.Column;
import com.suspend.annotation.Id;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableMapperTest {

    public static final class TestModel {
        @Id
        @Column(name = "id")
        private Integer stagod;
        @Column
        private String name;
        @Column
        private int age;

        private String notMapped;
    }

    @Test
    void testMap() {
        TableMapper mapper = new TableMapper();

        TableMetadata metadata = mapper.getMetadata(TestModel.class);
        assertEquals("TestModel", metadata.getTableName());
        assertEquals(3, metadata.getColumns().size());
        assertEquals(1, metadata.getIdColumns().size());
        assertEquals(Integer.class, metadata.getIdColumns().get(0).getType());
        assertEquals("stagod", metadata.getColumns().get(0).getName());
        assertEquals("id", metadata.getColumns().get(0).getColumnName());
    }
}