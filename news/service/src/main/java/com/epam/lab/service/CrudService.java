package com.epam.lab.service;

public interface CrudService<T> {
    void create(T dto);
    T read(long id);
    void update(T dto);
    void delete(long id);
}
