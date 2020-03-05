package com.epam.lab.dao;

import com.epam.lab.model.Author;

import java.util.List;

public interface AuthorDao extends CrudDao<Author> {
    List<Long> findNewsByAuthorId(long authorId);

    List<Long> findNewsByAuthorName(String authorName);

    List<Long> findNewsByAuthorSurname(String authorSurname);

    List<Author> getAll();

}
