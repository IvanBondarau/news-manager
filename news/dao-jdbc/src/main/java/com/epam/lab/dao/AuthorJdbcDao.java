package com.epam.lab.dao;

import com.epam.lab.exception.AuthorNotFoundException;
import com.epam.lab.model.Author;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class AuthorJdbcDao extends AbstractDao implements AuthorDao {

    private static final Logger logger = Logger.getLogger(AuthorJdbcDao.class);

    private static final String INSERT_STATEMENT =
            "INSERT INTO public.author(name, surname) VALUES(?, ?)";

    private static final String SELECT_BY_ID_STATEMENT =
            "SELECT id, name, surname FROM public.author WHERE id = ?";

    private static final String SELECT_ALL_STATEMENT =
            "SELECT id, name, surname FROM public.author";

    private static final String UPDATE_BY_ID_STATEMENT =
            "UPDATE public.author " +
                    "SET name = ?, surname = ? " +
                    "WHERE id = ?";

    private static final String DELETE_BY_ID_STATEMENT =
            "DELETE FROM public.author WHERE id = ?";

    private static final String SELECT_NEWS_ID_BY_AUTHOR_STATEMENT =
            "SELECT news_id FROM news_author WHERE author_id = ?";

    private static final String SELECT_BY_NAME_STATEMENT =
            "SELECT news_id FROM news_author JOIN author ON author.id = author_id WHERE author.name = ?";

    private static final String SELECT_BY_SURNAME_STATEMENT =
            "SELECT news_id FROM news_author JOIN author ON author.id = author_id WHERE author.surname = ?";


    @Autowired
    public AuthorJdbcDao(DataSource dataSource) {
        super.setDataSource(dataSource);
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
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_BY_ID_STATEMENT,
                    new Object[]{id},
                    new AuthorRowMapper()
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            logger.error("Author read error: author not found");
            logger.error("Author id = " + id);
            throw new AuthorNotFoundException(id);
        }


    }

    @Override
    public void update(Author entity) {

        long numOfUpdated = jdbcTemplate.update(UPDATE_BY_ID_STATEMENT,
                entity.getName(),
                entity.getSurname(),
                entity.getId());

        if (numOfUpdated != 1) {
            logger.error("Author update error: author not found");
            logger.error("Author = " + entity);
            throw new AuthorNotFoundException(entity.getId());
        }

    }

    @Override
    public void delete(long id) {
        long numOfDeleted = jdbcTemplate.update(DELETE_BY_ID_STATEMENT, id);
        if (numOfDeleted != 1) {
            logger.error("Author delete error: author not found");
            logger.error("Author id = " + id);
            throw new AuthorNotFoundException(id);
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
    public List<Long> getNewsIdByAuthorName(String authorName) {
        System.out.println(authorName);
        return jdbcTemplate.query(
                SELECT_BY_NAME_STATEMENT,
                new Object[]{authorName},
                ((resultSet, i) -> resultSet.getLong(1))
        );
    }


    @Override
    public List<Long> getNewsIdByAuthorSurname(String authorSurname) {
        return jdbcTemplate.query(
                SELECT_BY_SURNAME_STATEMENT,
                new Object[]{authorSurname},
                ((resultSet, i) -> resultSet.getLong(1))
        );
    }

    @Override
    public List<Author> getAll() {
        return jdbcTemplate.query(SELECT_ALL_STATEMENT, new AuthorRowMapper());
    }

    private static final class AuthorRowMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);

            return new Author(id, name, surname);
        }
    }


}
