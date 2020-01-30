package com.epam.lab.exception;

public class AuthorNotFoundException extends RuntimeException {

    private long authorId;

    public AuthorNotFoundException(String message) {
        super(message);
    }

    public AuthorNotFoundException(long authorId, String message) {
        super(message);
    }

    public long getAuthorId() {
        return authorId;
    }
}
