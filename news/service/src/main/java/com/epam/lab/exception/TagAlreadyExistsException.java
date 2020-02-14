package com.epam.lab.exception;

public class TagAlreadyExistsException extends RuntimeException{

    private long tagId;
    private String name;

    public TagAlreadyExistsException(long tagId, String name) {
        super();
        this.tagId = tagId;
        this.name = name;
    }

    public long getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }
}
