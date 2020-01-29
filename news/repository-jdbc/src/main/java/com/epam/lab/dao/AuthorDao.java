package com.epam.lab.dao;

import com.epam.lab.entity.Author;

import java.util.List;

public interface AuthorDao extends CrudDao<Author> {
    List<Long> getNewsIdByAuthor(Author author);
    Author getAuthorByNewsId(long newsId);
}
