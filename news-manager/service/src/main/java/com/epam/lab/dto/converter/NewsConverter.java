package com.epam.lab.dto.converter;

import com.epam.lab.dto.AuthorDto;
import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.TagDto;
import com.epam.lab.exception.InvalidNumberOfAuthorsException;
import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class NewsConverter implements EntityDtoConverter<News, NewsDto> {

    @Autowired
    private AuthorConverter authorConverter;
    @Autowired
    private TagConverter tagConverter;

    public NewsConverter(AuthorConverter authorConverter, TagConverter tagConverter) {
        this.authorConverter = authorConverter;
        this.tagConverter = tagConverter;
    }

    @Override
    public NewsDto convertToDto(News entity) {
        NewsDto newsDto = new NewsDto();
        newsDto.setId(entity.getId());
        newsDto.setTitle(entity.getTitle());
        newsDto.setShortText(entity.getShortText());
        newsDto.setFullText(entity.getFullText());
        newsDto.setCreationDate(entity.getCreationDate());
        newsDto.setModificationDate(entity.getModificationDate());

        newsDto.setTags(convertTagsToDto(entity.getTags()));
        newsDto.setAuthor(convertAuthorsToDto(entity.getAuthors()));

        return newsDto;
    }

    @Override
    public News convertToEntity(NewsDto dto) {
        News news = new News();
        news.setId(dto.getId());
        news.setTitle(dto.getTitle());
        news.setShortText(dto.getShortText());
        news.setFullText(dto.getFullText());
        news.setCreationDate(dto.getCreationDate());
        news.setModificationDate(dto.getModificationDate());

        news.setAuthors(convertAuthorDtoToEntity(dto.getAuthor()));
        news.setTags(convertTagDtoToEntities(dto.getTags()));

        return news;
    }

    private Set<Author> convertAuthorDtoToEntity(AuthorDto authorDto) {
        if (authorDto == null) {
            return new HashSet<>();
        }

        return Collections.singleton(authorConverter.convertToEntity(authorDto));
    }

    private Set<Tag> convertTagDtoToEntities(Set<TagDto> tags) {
        if (tags == null || tags.isEmpty()) {
            return new HashSet<>();
        }

        return tags.stream()
                .map(t -> tagConverter.convertToEntity(t))
                .collect(Collectors.toSet());
    }

    private AuthorDto convertAuthorsToDto(Set<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return null;
        }

        if (authors.size() != 1) {
            throw new InvalidNumberOfAuthorsException("News must contain only one author, but "
                    + authors.size() + " found");
        }

        AuthorDto result = null;

        for (Author author : authors) {
            result = authorConverter.convertToDto(author);
        }

        return result;
    }


    private Set<TagDto> convertTagsToDto(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return new HashSet<>();
        }

        return tags.stream()
                .map(t -> tagConverter.convertToDto(t))
                .collect(Collectors.toSet());
    }
}