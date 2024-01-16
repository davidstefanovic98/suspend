package com.suspend.entitymanager;

import com.suspend.reflection.TableMapper;
import com.suspend.reflection.TableMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityContainer {

    private List<EntityReference> entityReferences = new ArrayList<>();

    public EntityContainer() {}

    public EntityContainer(List<EntityReference> entityReferences) {
        this.entityReferences = entityReferences;
    }

    public List<EntityReference> getEntityReferences() {
        return entityReferences;
    }

    public void addEntityReference(EntityReference entityReference) {
        entityReferences.add(entityReference);
    }

    public void setEntityReferences(List<EntityReference> entityReferences) {
        this.entityReferences = entityReferences;
    }

    public EntityReference resolve(Class<?> entityClass) {
        Optional<EntityReference> entityReferenceOptional = entityReferences.stream()
                .filter(entityReference -> entityReference.getEntityClass().equals(entityClass))
                .findFirst();

        if (entityReferenceOptional.isPresent()) {
            return entityReferenceOptional.get();
        } else {
            EntityReference entityReference = new EntityReference(entityClass);
            entityReference.setTableMetadata(new TableMapper().getMetadata(entityClass));
            entityReferences.add(entityReference);
            return entityReference;
        }
    }

    public <T> EntityReference resolve(T entity) {
        Optional<EntityReference> entityReferenceOptional = entityReferences.stream()
                .filter(entityReference -> entityReference.getEntityClass().equals(entity.getClass()))
                .findFirst();

        if (entityReferenceOptional.isPresent()) {
            return entityReferenceOptional.get();
        } else {
            EntityReference entityReference = new EntityReference(entity.getClass());
            entityReference.setTableMetadata(new TableMapper().getMetadata(entity));
            entityReferences.add(entityReference);
            return entityReference;
        }
    }

    public EntityReference initialize(EntityReference entityReference) {
        if (entityReference.isFullyInitialized()) {
            return entityReference;
        }

        entityReference.getManyToOneReferences().forEach(this::initialize);
        entityReference.getOneToManyReferences().forEach(this::initialize);
        entityReference.setFullyInitialized(true);

        return entityReference;
    }
}
