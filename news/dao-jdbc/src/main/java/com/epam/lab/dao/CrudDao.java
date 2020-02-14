package com.epam.lab.dao;

import com.epam.lab.model.Entity;

public interface CrudDao<T extends Entity> {
    long create(T entity);
    T read(long id);
    void update(T entity);
    void delete(long id);
}
