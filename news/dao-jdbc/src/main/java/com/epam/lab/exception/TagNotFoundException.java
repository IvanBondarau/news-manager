package com.epam.lab.exception;

public class TagNotFoundException extends RuntimeException {

    private long tagId;

    public TagNotFoundException(String message, long tagId) {
        super(message);
        this.tagId = tagId;
    }

    public long getTagId() {
        return tagId;
    }
}
