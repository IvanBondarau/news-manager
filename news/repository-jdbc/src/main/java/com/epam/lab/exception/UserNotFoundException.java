package com.epam.lab.exception;

public class UserNotFoundException extends RuntimeException {

    private long userId;

    public UserNotFoundException(long userId, String message) {
        super(message);
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
