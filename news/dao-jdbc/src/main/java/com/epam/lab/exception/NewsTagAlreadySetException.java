package com.epam.lab.exception;

public class NewsTagAlreadySetException extends RuntimeException {

    private long newsId;
    private long tagId;

    public NewsTagAlreadySetException(String message, long newsId, long tagId) {
        super(message);
        this.newsId = newsId;
        this.tagId = tagId;
    }

    public long getNewsId() {
        return newsId;
    }

    public long getTagId() {
        return tagId;
    }
}
