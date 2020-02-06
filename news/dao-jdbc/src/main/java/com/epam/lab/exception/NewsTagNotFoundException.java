package com.epam.lab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
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
