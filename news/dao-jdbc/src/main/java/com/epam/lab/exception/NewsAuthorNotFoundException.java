package com.epam.lab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
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
