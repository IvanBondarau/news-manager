package com.epam.lab.dao;

import com.epam.lab.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao extends CrudDao<Author> {
    List<Long> getNewsIdByAuthor(long authorId);

    List<Long> getNewsIdByAuthorName(String authorName);

    List<Long> getNewsIdByAuthorSurname(String authorSurname);

}
