package com.epam.lab.converter;

import com.epam.lab.dto.NewsDto;
import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsConverter implements EntityDtoConverter<News, NewsDto> {

    @Autowired
    private AuthorConverter authorConverter;
    @Autowired
    private TagConverter tagConverter;

    @Override
    public NewsDto convertToDto(News entity) {
        NewsDto newsDto = new NewsDto();
        newsDto.setId(entity.getId());
        newsDto.setTitle(entity.getTitle());
        newsDto.setShortText(entity.getShortText());
        newsDto.setFullText(entity.getFullText());
        newsDto.setCreationDate(entity.getCreationDate());
        newsDto.setModificationDate(entity.getModificationDate());
        return newsDto;
    }

    @Override
    public News convertToEntity(NewsDto dto) {
        return new News(dto.getId(), dto.getTitle(),
                dto.getShortText(), dto.getFullText(),
                dto.getCreationDate(), dto.getModificationDate());
    }
}
