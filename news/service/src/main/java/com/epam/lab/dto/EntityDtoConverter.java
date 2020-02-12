package com.epam.lab.dto;

import com.epam.lab.model.Entity;

public interface EntityDtoConverter<EntityType extends Entity, DtoType extends Dto> {
    DtoType convertToDto(EntityType entity);
    EntityType convertToEntity(DtoType dto);
}