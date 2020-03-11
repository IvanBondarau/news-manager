package com.epam.lab.dto.converter;

import com.epam.lab.dto.Dto;

public interface EntityDtoConverter<E, D extends Dto> {
    D convertToDto(E entity);

    E convertToEntity(D dto);
}
