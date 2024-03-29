package com.epam.lab.dao;

public interface CrudDao<T> {
    void create(T entity);
    T read(long id);
    void update(T entity);
    void delete(long id);
}
