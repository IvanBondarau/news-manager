package com.epam.lab.exception;

import com.epam.lab.model.EntityType;

public class DataEntityNotFoundException extends RuntimeException {

    private EntityType entityType;
    private Long id;

    public DataEntityNotFoundException(EntityType entityType, Long id) {
        this.entityType = entityType;
        this.id = id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public long getId() {
        return id;
    }
}
