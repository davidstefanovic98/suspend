package com.suspend.executor;

import com.suspend.querybuilder.DefaultQueryBuilder;
import com.suspend.querybuilder.QueryBuilder;
import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorTest {

    public static final class TestModel {
        private String name;
        private int age;
    }

    @Test
    void testExecute() {
        Executor executor = new Executor();
        TableMapper mapper = new TableMapper();
        TableMetadata metadata = mapper.getMetadata(TestModel.class);
        QueryBuilder builder = new DefaultQueryBuilder(metadata);
        List<Map<String, Object>> list = executor.execute(builder.select().build());
        assertEquals("David", list.get(0).get("name"));
    }

//    @Test
//    void testExecuteUpdate() {
//        Executor executor = new Executor();
//        ModelMapper mapper = new ModelMapper();
//        ModelMetadata metadata = mapper.getMetadata(ExecutorTest.TestModel.class);
//        QueryBuilder builder = new DefaultQueryBuilder(metadata);
//        builder.update();
//        Long rowsAffected = executor.executeUpdate(builder.build());
//        assertEquals(1, rowsAffected);
//    }
}