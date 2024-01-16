package com.suspend.repository;

import com.suspend.configuration.Configuration;
import com.suspend.proxy.RepositoryInvocationHandler;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositoryFactory {

    private final Map<Class<?>, Repository<?, ?>> map = new HashMap<>();

    public RepositoryFactory(Configuration configuration) {
        createRepository(configuration);
    }

    private void createRepository(Configuration configuration) {
        configuration.getRepositoryMap().forEach((k, v) -> {
            if (v != null) {
                RepositoryImpl<?, ?> repository = new RepositoryImpl<>(k, configuration.getEntityContainer());
                Object repository1 = Proxy.newProxyInstance(
                        RepositoryFactory.class.getClassLoader(),
                        new Class[]{v},
                        new RepositoryInvocationHandler(repository)
                );
                map.put(k, (Repository<?, ?>) repository1);
            }
        });
    }

    public <T> Repository<T, ?> getRepository(Class<T> entityClass) {
        return (Repository<T, ?>) map.get(entityClass);
    }

    public <T> List<Repository<T, ?>> getRepositories() {
        return map.values()
                .stream()
                .map(repository -> (Repository<T, ?>) repository)
                .collect(Collectors.toList());
    }
}
