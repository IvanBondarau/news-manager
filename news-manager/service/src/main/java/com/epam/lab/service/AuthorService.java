package com.epam.lab.service;

import com.epam.lab.dto.AuthorDto;

public interface AuthorService extends CrudService<AuthorDto> {
    void save(AuthorDto authorDto);
}
