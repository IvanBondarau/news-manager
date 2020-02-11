package com.epam.lab.exception;

public class TagNotFoundException extends ItemNotFoundException {
    public TagNotFoundException(long id) {
        super(id);
    }
}
