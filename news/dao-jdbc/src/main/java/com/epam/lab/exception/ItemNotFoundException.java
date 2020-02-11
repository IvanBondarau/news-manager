package com.epam.lab.exception;

public class ItemNotFoundException extends RuntimeException {
    private long id;

    public ItemNotFoundException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
