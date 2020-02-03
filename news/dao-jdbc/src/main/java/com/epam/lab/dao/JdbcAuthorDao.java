package com.epam.lab.dao;

import com.epam.lab.DataSourceHolder;
import com.epam.lab.exception.AuthorNotFoundException;
import com.epam.lab.model.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcAuthorDao extends AbstractDao implements AuthorDao {


    private static final String INSERT_STATEMENT =
            "INSERT INTO public.author(name, surname) VALUES(?, ?)";

    private static final String SELECT_BY_ID_STATEMENT =
            "SELECT id, name, surname FROM public.author WHERE id = ?";

    private static final String UPDATE_BY_ID_STATEMENT =
            "UPDATE public.author " +
                    "SET name = ?, surname = ? " +
                    "WHERE id = ?";

    private static final String DELETE_BY_ID_STATEMENT =
            "DELETE FROM public.author WHERE id = ?";

    private static final String SELECT_NEWS_ID_BY_AUTHOR_STATEMENT =
            "SELECT news_id FROM news_author WHERE author_id = ?";

    private static final String SELECT_BY_NAME_SURNAME_STATEMENT =
            "SELECT id, name, surname FROM public.author WHERE name = ? AND surname = ?";


    @Autowired
    public JdbcAuthorDao(@Qualifier(value = "dataSourceHolder") DataSourceHolder dataSourceHolder) {
        super.setDataSource(dataSourceHolder.getDataSource());
    }


    @Override
    public long create(Author entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((Connection connection) -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT,
                            Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, entity.getName());
                    preparedStatement.setString(2, entity.getSurname());
                    return preparedStatement;
                },
                keyHolder
        );
        long key = (long) keyHolder.getKeys().get("id");
        entity.setId(key);
        return key;
    }

    @Override
    public Author read(long id) {
        return jdbcTemplate.queryForObject(
                SELECT_BY_ID_STATEMENT,
                new Object[]{id},
                new AuthorMapper()
        );

    }

    @Override
    public void update(Author entity) {

        long numOfUpdated = jdbcTemplate.update(UPDATE_BY_ID_STATEMENT,
                entity.getName(),
                entity.getSurname(),
                entity.getId());

        if (numOfUpdated != 1) {
            throw new AuthorNotFoundException(entity.getId(), "Author with id " + entity.getId() + " not found");
        }

    }

    @Override
    public void delete(long id) {
        long numOfDeleted = jdbcTemplate.update(DELETE_BY_ID_STATEMENT, id);
        if (numOfDeleted != 1) {
            throw new AuthorNotFoundException(id, "Author with id " + id + " not found");
        }
    }

    @Override
    public List<Long> getNewsIdByAuthor(long authorId) {
        return jdbcTemplate.query(
                SELECT_NEWS_ID_BY_AUTHOR_STATEMENT,
                new Object[]{authorId},
                ((resultSet, i) -> resultSet.getLong(1))
        );
    }

    @Override
    public Optional<Author> findByNameSurname(String name, String surname) {
        List<Author> loadedAuthors = jdbcTemplate.query(
                SELECT_BY_NAME_SURNAME_STATEMENT,
                new Object[]{name, surname},
                new AuthorMapper()
        );

        if (loadedAuthors.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(loadedAuthors.get(0));
        }
    }


    private static final class AuthorMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);

            return new Author(id, name, surname);
        }
    }


}
