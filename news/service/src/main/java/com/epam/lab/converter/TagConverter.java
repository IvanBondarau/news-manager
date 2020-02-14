package com.epam.lab.converter;

import com.epam.lab.dto.TagDto;
import com.epam.lab.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagConverter implements EntityDtoConverter<Tag, TagDto> {
    @Override
    public TagDto convertToDto(Tag entity) {
        TagDto tagDto = new TagDto();
        tagDto.setId(entity.getId());
        tagDto.setName(entity.getName());
        return tagDto;
    }

    @Override
    public Tag convertToEntity(TagDto dto) {
        return new Tag(dto.getId(), dto.getName());
    }
}
