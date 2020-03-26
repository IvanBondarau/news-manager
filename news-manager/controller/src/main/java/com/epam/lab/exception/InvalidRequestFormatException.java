package com.epam.lab.exception;

public class InvalidRequestFormatException extends RuntimeException {
    public InvalidRequestFormatException(String message) {
        super(message);
    }

}
