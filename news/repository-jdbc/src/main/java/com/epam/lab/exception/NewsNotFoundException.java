package com.epam.lab.exception;

public class NewsNotFoundException extends RuntimeException {

    private long newsId;

    public NewsNotFoundException(String message, long newsId) {
        super(message);
        this.newsId = newsId;
    }

    public long getTagId() {
        return newsId;
    }
}