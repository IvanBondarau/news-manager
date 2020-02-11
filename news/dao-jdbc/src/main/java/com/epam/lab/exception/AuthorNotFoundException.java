package com.epam.lab.exception;

public class AuthorNotFoundException extends ItemNotFoundException {
    public AuthorNotFoundException(long id) {
        super(id);
    }
}
