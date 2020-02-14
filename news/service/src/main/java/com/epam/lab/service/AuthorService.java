package com.epam.lab.service;

import com.epam.lab.dto.AuthorDto;
import com.epam.lab.model.Author;

public interface AuthorService extends CrudService<AuthorDto> {
    void upload(AuthorDto authorDto);
}
