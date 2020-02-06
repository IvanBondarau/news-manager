package com.epam.lab.exception;

public class NewsAuthorAlreadySetException extends RuntimeException {

    private long newsId;

    public NewsAuthorAlreadySetException(String message, long newsId) {
        super(message);
        this.newsId = newsId;
    }

    public long getNewsId() {
        return newsId;
    }
}
