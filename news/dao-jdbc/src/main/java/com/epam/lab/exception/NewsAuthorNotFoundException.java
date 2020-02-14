package com.epam.lab.exception;

public class NewsAuthorNotFoundException extends RuntimeException {

    public NewsAuthorNotFoundException() {
    }

    public NewsAuthorNotFoundException(String message) {
        super(message);
    }

    public NewsAuthorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NewsAuthorNotFoundException(Throwable cause) {
        super(cause);
    }
}
