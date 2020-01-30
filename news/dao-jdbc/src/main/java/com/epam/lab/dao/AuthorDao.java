package com.epam.lab.dao;

import com.epam.lab.model.Author;

import java.util.List;

public interface AuthorDao extends CrudDao<Author> {
    List<Long> getNewsIdByAuthor(Author author);
}
