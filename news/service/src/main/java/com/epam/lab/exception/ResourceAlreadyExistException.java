package com.epam.lab.exception;

public class ResourceAlreadyExistException extends RuntimeException{

    private long resourceId;

    public ResourceAlreadyExistException(String message, long resourceId) {
        super(message);
        this.resourceId = resourceId;
    }

    public long getResourceId() {
        return resourceId;
    }
}
