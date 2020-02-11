package com.epam.lab.dto;

import com.epam.lab.model.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorConverter implements EntityDtoConverter<Author, AuthorDto> {

    @Override
    public AuthorDto convertToDto(Author entity) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(entity.getId());
        authorDto.setName(entity.getName());
        authorDto.setSurname(entity.getSurname());
        return authorDto;
    }

    @Override
    public Author convertToEntity(AuthorDto dto) {
        return new Author(dto.getId(), dto.getName(), dto.getSurname());
    }
}
