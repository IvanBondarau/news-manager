package com.epam.lab.exception;

public class UserNotFoundException extends ItemNotFoundException {

    public UserNotFoundException(long id) {
        super(id);
    }
}
