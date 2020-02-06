package com.epam.lab.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchCriteria {

    private Long authorId;
    private String authorName;
    private String authorSurname;

    private Set<String> tagNames;

    private List<SortOrder> sortParams;


    public SearchCriteria() {
        tagNames = new HashSet<>();
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorSurname() {
        return authorSurname;
    }

    public void setAuthorSurname(String authorSurname) {
        this.authorSurname = authorSurname;
    }

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }

    public List<SortOrder> getSortParams() {
        return sortParams;
    }

    public void setSortParams(List<SortOrder> sortParams) {
        this.sortParams = sortParams;
    }
}
