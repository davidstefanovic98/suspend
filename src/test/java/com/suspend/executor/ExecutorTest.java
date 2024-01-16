package com.suspend.executor;

import com.suspend.annotation.*;
import com.suspend.configuration.Configuration;
import com.suspend.configuration.entity.TestModel;
import com.suspend.entitymanager.EntityContainer;
import com.suspend.entitymanager.EntityReference;
import com.suspend.querybuilder.DefaultQueryBuilder;
import com.suspend.mapper.Mapper;
import com.suspend.querybuilder.QueryBuilder;
import com.suspend.reflection.TableMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ExecutorTest {

    @Test
    void testExecute() {
        Executor executor = new Executor();
        TableMapper mapper = new TableMapper();
        Configuration configuration = Configuration.getInstance();
        configuration.setRepositoryPackageName("com.suspend.configuration.repository");
        configuration.setEntityPackageName("com.suspend.configuration.entity");
        configuration.addAnnotatedClass(Table.class);

        EntityContainer entityContainer = configuration.getEntityContainer();
        EntityReference entityReference = entityContainer.resolve(TestModel.class);
        QueryBuilder builder = new DefaultQueryBuilder(entityReference.getTableMetadata());
        builder.select().join();
        List<Map<String, Object>> result = executor.execute(builder.build());
        List<com.suspend.configuration.entity.TestModel> testModels = new Mapper<>(com.suspend.configuration.entity.TestModel.class, result).map();
//        assertThrows(InvalidMappingException.class, () -> new Mapper<>(TestModel.class, result).map());
    }
}