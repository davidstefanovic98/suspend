package com.suspend.executor;

import com.suspend.annotation.Column;
import com.suspend.annotation.Id;
import com.suspend.annotation.Table;
import com.suspend.querybuilder.DefaultQueryBuilder;
import com.suspend.querybuilder.QueryBuilder;
import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorTest {

    @Table(name = "test_model")
    public static final class TestModel {
        @Id
        @Column(name = "id")
        private Integer whatever;
        @Column
        private String name;
        @Column
        private int age;
    }

    @Test
    void testExecute() {
        Executor executor = new Executor();
        TableMapper mapper = new TableMapper();
        TableMetadata metadata = mapper.getMetadata(TestModel.class);
        QueryBuilder builder = new DefaultQueryBuilder(metadata);
        List<Map<String, Object>> list = executor.execute(builder.select().build());
        assertEquals("John", list.get(0).get("name"));
    }
}