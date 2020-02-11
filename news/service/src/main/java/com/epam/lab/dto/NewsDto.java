package com.epam.lab.dto;

import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class NewsDto implements Dto {

    long id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTags(Set<TagDto> tags) {
        this.tags = tags;
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
        return tags;
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

    @Override
    public String toString() {
        return "NewsDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", shortText='" + shortText + '\'' +
                ", fullText='" + fullText + '\'' +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                ", tags=" + tags +
                ", author=" + author +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsDto dto = (NewsDto) o;
        return id == dto.id &&
                Objects.equals(title, dto.title) &&
                Objects.equals(shortText, dto.shortText) &&
                Objects.equals(fullText, dto.fullText) &&
                Objects.equals(creationDate, dto.creationDate) &&
                Objects.equals(modificationDate, dto.modificationDate) &&
                Objects.equals(tags, dto.tags) &&
                Objects.equals(author, dto.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, shortText, fullText, creationDate, modificationDate, tags, author);
    }
}
