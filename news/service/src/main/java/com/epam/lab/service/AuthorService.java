package com.epam.lab.service;

import com.epam.lab.dto.AuthorDto;
import com.epam.lab.model.Author;

import java.util.List;
import java.util.Set;

public interface AuthorService extends CrudService<AuthorDto> {
    void save(AuthorDto authorDto);
}
