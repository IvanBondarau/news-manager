package com.epam.lab.dao;

import com.epam.lab.entity.Author;
import com.epam.lab.exception.AuthorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class AuthorDaoImpl extends AbstractDao implements AuthorDao {

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

    private static final String SELECT_BY_NEWS_ID_STATEMENT =
            "SELECT author_id FROM news_author WHERE news_id = ?";

    @Autowired
    public AuthorDaoImpl(DataSource dataSource) {
        super(dataSource);
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
        long key = (long) keyHolder.getKey();
        entity.setId(key);
        return key;
    }

    @Override
    public Author read(long id) {
        List<Author> loadedAuthors = jdbcTemplate.query(
                SELECT_BY_ID_STATEMENT,
                new Object[]{id},
                new AuthorMapper()
        );

        if (loadedAuthors.size() == 0) {
            throw new AuthorNotFoundException(id, "Author with id " + id + " not found");
        }
        return loadedAuthors.get(0);
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
    public List<Long> getNewsIdByAuthor(Author author) {
        return jdbcTemplate.query(
                SELECT_NEWS_ID_BY_AUTHOR_STATEMENT,
                new Object[]{author.getId()},
                ((resultSet, i) -> resultSet.getLong(1))
        );
    }

    @Override
    public Author getAuthorByNewsId(long newsId) {
        List<Long> authorsId = jdbcTemplate.query(
                SELECT_BY_NEWS_ID_STATEMENT,
                new Object[]{newsId},
                ((resultSet, i) -> resultSet.getLong(1))
        );

        if (authorsId.size() < 1) {
            throw new AuthorNotFoundException("Author for news with id " + newsId + " not found");
        }

        return read(authorsId.get(0));
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
