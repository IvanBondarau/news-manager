package com.epam.lab.dto;

import com.epam.lab.model.News;
import org.springframework.stereotype.Component;

@Component
public class NewsConverter implements EntityDtoConverter<News, NewsDto> {

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
