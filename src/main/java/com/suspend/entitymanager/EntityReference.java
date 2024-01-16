package com.suspend.entitymanager;

import com.suspend.reflection.TableMetadata;

import java.util.ArrayList;
import java.util.List;

public class EntityReference {
    private Class<?> entityClass;
    private List<EntityReference> manyToOneReferences = new ArrayList<>();
    private List<EntityReference> oneToManyReferences = new ArrayList<>();
    private boolean isInitializing;
    private boolean isFullyInitialized;
    private TableMetadata tableMetadata;
    private EntityReference parent;
    private boolean isOwner;

    public EntityReference() {}

    public EntityReference(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityReference(Class<?> entityClass, List<EntityReference> manyToOneReferences, List<EntityReference> oneToManyReferences) {
        this.entityClass = entityClass;
        this.manyToOneReferences = manyToOneReferences;
        this.oneToManyReferences = oneToManyReferences;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public List<EntityReference> getManyToOneReferences() {
        return manyToOneReferences;
    }

    public List<EntityReference> getOneToManyReferences() {
        return oneToManyReferences;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public void setManyToOneReferences(List<EntityReference> manyToOneReferences) {
        this.manyToOneReferences = manyToOneReferences;
    }

    public void setOneToManyReferences(List<EntityReference> oneToManyReferences) {
        this.oneToManyReferences = oneToManyReferences;
    }

    public TableMetadata getTableMetadata() {
        return tableMetadata;
    }

    public void setTableMetadata(TableMetadata tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    public boolean isInitializing() {
        return isInitializing;
    }

    public void setInitializing(boolean initializing) {
        isInitializing = initializing;
    }

    public boolean isFullyInitialized() {
        return isFullyInitialized;
    }

    public void setFullyInitialized(boolean fullyInitialized) {
        isFullyInitialized = fullyInitialized;
    }

    public EntityReference getParent() {
        return parent;
    }

    public void setParent(EntityReference parent) {
        this.parent = parent;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}
