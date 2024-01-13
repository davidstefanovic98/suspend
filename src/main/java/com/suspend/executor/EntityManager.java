package com.suspend.executor;

import com.suspend.querybuilder.DefaultQueryBuilder;
import com.suspend.querybuilder.Mapper;
import com.suspend.reflection.TableMapper;

import java.util.List;
import java.util.Map;

public class EntityManager {

    private final Executor executor;

    public EntityManager() {
        this.executor = new Executor();
    }

    private <T> List<T> executeQuery(Class<T> clazz, String query) {
        List<Map<String, Object>> data = executor.execute(query);
        return new Mapper<>(clazz, data).map();
    }

    public <T> List<T> findAll(Class<T> clazz) {
        DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder(new TableMapper().getMetadata(clazz));
        return executeQuery(clazz, queryBuilder.select().build());
    }
}
