package com.epam.lab.service;

import com.epam.lab.dto.Dto;

import java.util.List;

public interface CrudService<T extends Dto> {
    void create(T dto);

    T read(long id);

    void update(T dto);

    void delete(long id);

    List<T> getAll();
}
