package com.epam.lab.exception;


public class ResourceNotFoundException extends RuntimeException {

    private long resourceId;

    public ResourceNotFoundException(String message, long resourceId) {
        super(message);
        this.resourceId = resourceId;
    }

    public ResourceNotFoundException(String message, Throwable cause, long resourceId) {
        super(message, cause);
        this.resourceId = resourceId;
    }

    public long getResourceId() {
        return resourceId;
    }
}
