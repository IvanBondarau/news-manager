package com.epam.lab.service;

import com.epam.lab.dto.TagDto;

public interface TagService extends CrudService<TagDto> {

    void save(TagDto tagDto);
}
