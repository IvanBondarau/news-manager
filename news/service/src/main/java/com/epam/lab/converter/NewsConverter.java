package com.epam.lab.converter;

import com.epam.lab.dto.NewsDto;
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

        newsDto.setTags(
                entity.getTags() == null ? new HashSet<>() :
                entity.getTags()
                        .stream()
                        .map(t -> tagConverter.convertToDto(t))
                        .collect(Collectors.toSet())
        );

        newsDto.setAuthor(
                entity.getAuthors() == null ? null :
                entity.getAuthors().isEmpty() ? null : authorConverter.convertToDto(entity.getAuthors().get(0))
        );
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
        news.setAuthors(dto.getAuthor() == null ? new LinkedList<>()
                : Collections.singletonList(
                        authorConverter.convertToEntity(dto.getAuthor())));
        news.setTags(dto.getTags() == null ? new LinkedList<>()
                : dto.getTags().stream()
                                .map(t -> tagConverter.convertToEntity(t))
                                .collect(Collectors.toList())
        );

        return news;
    }
}