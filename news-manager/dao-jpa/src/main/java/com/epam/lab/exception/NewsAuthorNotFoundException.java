package com.epam.lab.exception;

public class NewsAuthorNotFoundException extends RuntimeException {

    public NewsAuthorNotFoundException(String message) {
        super(message);
    }
}
