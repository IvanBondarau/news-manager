package com.epam.lab.exception;

public class InvalidNumberOfAuthorsException extends RuntimeException {
    public InvalidNumberOfAuthorsException(String message) {
        super(message);
    }
}
