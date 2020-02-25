package com.epam.lab.dto.converter;

import com.epam.lab.dto.Dto;

public interface EntityDtoConverter<EntityType, DtoType extends Dto> {
    DtoType convertToDto(EntityType entity);

    EntityType convertToEntity(DtoType dto);
}
