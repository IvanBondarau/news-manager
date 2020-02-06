package com.epam.lab.exception;

public class TagAlreadyExistException extends RuntimeException{

    private long tagId;
    private String name;

    public TagAlreadyExistException(long tagId, String name) {
        super();
        this.tagId = tagId;
        this.name = name;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
