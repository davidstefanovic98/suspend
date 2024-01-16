package com.suspend.repository;

import com.suspend.annotation.Exclude;
import com.suspend.annotation.Id;
import com.suspend.converter.AttributeConverter;
import com.suspend.entitymanager.EntityContainer;
import com.suspend.executor.Executor;
import com.suspend.querybuilder.DefaultQueryBuilder;
import com.suspend.mapper.Mapper;
import com.suspend.reflection.TableMapper;
import com.suspend.exception.NotUniqueResultException;
import com.suspend.util.ReflectionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Exclude
class RepositoryImpl<T, ID> implements Repository<T, ID> {

    private final Executor executor;
    private final Class<T> clazz;
    private final EntityContainer entityContainer;

    public RepositoryImpl(Class<T> clazz, EntityContainer entityContainer) {
        this.executor = new Executor();
        this.clazz = clazz;
        this.entityContainer = entityContainer;
    }

    @Override
    public List<T> findAll() {
        DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder(entityContainer.resolve(clazz).getTableMetadata());
        return executeQueryAndMap(clazz, queryBuilder.select().join().build());
    }

    @Override
    public T save(T model) {
        DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder(entityContainer.resolve(clazz).getTableMetadata());
        Object id = executor.executeUpdate(queryBuilder.insert().build());
        Arrays.stream(model.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .forEach(field -> {
                    Object result = AttributeConverter.convert(id, field.getType());
                    ReflectionUtil.setField(model, field, result);
                });
        return model;
    }

    @Override
    public T update(T model) {
        DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder(new TableMapper().getMetadata(model));
        executor.executeUpdate(queryBuilder.update().build());

        return model;
    }

    @Override
    public Optional<T> findById(ID id) {
        DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder(new TableMapper().getMetadata(clazz));
        List<T> result = executeQueryAndMap(clazz, queryBuilder.byId(id).build());
        if (result.size() > 1) {
            throw new NotUniqueResultException("The query returned more than one result.");
        }
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.get(0));
    }

    @Override
    public void deleteById(ID id) {
        DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder(new TableMapper().getMetadata(clazz));
        executor.executeUpdate(queryBuilder.deleteById(id).build());
    }

    /**
     * Executes the query and maps the result to the given class.
     * @param clazz The class to map the result to.
     * @param query The query to execute.
     * @return A list of objects of the given class.
     */
    private List<T> executeQueryAndMap(Class<T> clazz, String query) {
        List<Map<String, Object>> data = executor.execute(query);
        return new Mapper<>(clazz, data).map();
    }
}
