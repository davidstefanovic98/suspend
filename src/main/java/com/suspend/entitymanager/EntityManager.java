package com.suspend.entitymanager;

import com.suspend.configuration.Configuration;

import java.util.Map;

public class EntityManager {

    private final Map<Class<?>, Class<?>> repositoryMap;

    public EntityManager() {
        this.repositoryMap = Configuration.getInstance().getRepositoryMap();
    }


    public Map<Class<?>, Class<?>> getRepositoryMap() {
        return repositoryMap;
    }

}
