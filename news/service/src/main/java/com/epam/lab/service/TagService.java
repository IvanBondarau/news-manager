package com.epam.lab.service;

import com.epam.lab.dto.TagDto;

public interface TagService extends CrudService<TagDto> {

    void upload(TagDto tagDto);
}
