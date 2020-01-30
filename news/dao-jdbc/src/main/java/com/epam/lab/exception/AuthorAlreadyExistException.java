package com.epam.lab.exception;

public class AuthorAlreadyExistException extends RuntimeException {

    private long newsId;

    public AuthorAlreadyExistException(String message, long newsId) {
        super(message);
        this.newsId = newsId;
    }

    public long getNewsId() {
        return newsId;
    }
}
