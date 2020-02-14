package com.epam.lab.exception;

public class NewsTagNotFoundException extends RuntimeException {
    public NewsTagNotFoundException() {
    }

    public NewsTagNotFoundException(String message) {
        super(message);
    }

    public NewsTagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NewsTagNotFoundException(Throwable cause) {
        super(cause);
    }
}
