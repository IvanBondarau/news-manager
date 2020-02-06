package com.epam.lab.dao;

import com.epam.lab.exception.ResourceNotFoundException;

public interface CrudDao<T> {
    long create(T entity);
    T read(long id) throws ResourceNotFoundException;
    void update(T entity) throws ResourceNotFoundException;
    void delete(long id) throws ResourceNotFoundException;
}
