package com.epam.lab.exception;

public class NewsNotFoundException extends ItemNotFoundException {
    public NewsNotFoundException(long id) {
        super(id);
    }
}
