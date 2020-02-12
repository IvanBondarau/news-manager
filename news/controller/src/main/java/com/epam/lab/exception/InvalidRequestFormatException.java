package com.epam.lab.exception;

public class InvalidRequestFormatException extends RuntimeException {
    public InvalidRequestFormatException() {
    }

    public InvalidRequestFormatException(String message) {
        super(message);
    }

    public InvalidRequestFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequestFormatException(Throwable cause) {
        super(cause);
    }
}
