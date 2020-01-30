package com.epam.lab.dto;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class NewsDto {

    private String title;
    private String shortText;
    private String fullText;
    private Date creationDate;
    private Date modificationDate;

    private Set<TagDto> tags;
    private AuthorDto author;

    public NewsDto() {
        tags = new HashSet<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Set<TagDto> getTags() {
        return new HashSet<>(tags);
    }

    public void addTag(TagDto tag) {
        tags.add(tag);
    }

    public void removeTag(TagDto tag) {
        tags.remove(tag);
    }

    public AuthorDto getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDto author) {
        this.author = author;
    }


}
