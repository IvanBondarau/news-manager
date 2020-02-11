package com.epam.lab.service;

import com.epam.lab.dto.TagDto;

public interface TagService extends CrudService<TagDto> {

    void loadOrCreateTag(TagDto tagDto);
}
