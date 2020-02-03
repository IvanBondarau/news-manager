package com.epam.lab.dao;

import com.epam.lab.model.Author;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public interface AuthorDao extends CrudDao<Author> {
    List<Long> getNewsIdByAuthor(long authorId);
    Optional<Author> findByNameSurname(String name, String surname);
}
