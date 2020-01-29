package com.epam.lab.dao;

import com.epam.lab.entity.News;
import com.epam.lab.exception.NewsNotFoundException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class NewsDaoImpl extends AbstractDao implements NewsDao {

    private static final String INSERT_STATEMENT =
            "INSERT INTO public.news(title, short_text, full_text, creation_date, modification_date) "
                    + "VALUES(?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID_STATEMENT =
            "SELECT id, title, short_text, full_text, creation_date, modification_date "
                    + "FROM public.news WHERE id = ?";

    private static final String UPDATE_BY_ID_STATEMENT =
            "UPDATE public.news " +
                    "SET title = ?, short_text = ?, full_text = ?, creation_date = ?, modification_date = ? " +
                    "WHERE id = ?";

    private static final String DELETE_BY_ID_STATEMENT =
            "DELETE FROM public.news WHERE id = ?";

    public NewsDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long create(News entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((Connection connection) -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT,
                            Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, entity.getTitle());
                    preparedStatement.setString(2, entity.getShortText());
                    preparedStatement.setString(3, entity.getFullText());
                    preparedStatement.setDate(4, entity.getCreationDate());
                    preparedStatement.setDate(5, entity.getModificationDate());
                    return preparedStatement;
                },
                keyHolder
        );
        long key = (long) keyHolder.getKey();
        entity.setId(key);
        return key;
    }

    @Override
    public News read(long id) {
        List<News> loadedNews = jdbcTemplate.query(SELECT_BY_ID_STATEMENT,
                new Object[]{id},
                new NewsRowMapper());

        if (loadedNews.size() == 0) {
            throw new NewsNotFoundException("News with id " + id + " not found", id);
        }
        return loadedNews.get(0);
    }

    @Override
    public void update(News entity) {
        long numOfUpdated = jdbcTemplate.update(UPDATE_BY_ID_STATEMENT,
                entity.getTitle(),
                entity.getShortText(),
                entity.getFullText(),
                entity.getCreationDate(),
                entity.getModificationDate(),
                entity.getId());

        if (numOfUpdated != 1) {
            throw new NewsNotFoundException("News with id " + entity.getId() + " not found", entity.getId());
        }
    }

    @Override
    public void delete(long id) {
        long numOfDeleted = jdbcTemplate.update(DELETE_BY_ID_STATEMENT, id);
        if (numOfDeleted != 1) {
            throw new NewsNotFoundException("News with id " + id + " not found", id);
        }
    }

    private static final class NewsRowMapper implements RowMapper<News> {
        @Override
        public News mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong(1);
            String title = resultSet.getString(2);
            String shortText = resultSet.getString(3);
            String fullText = resultSet.getString(4);
            Date creationDate = resultSet.getDate(5);
            Date modificationDate = resultSet.getDate(6);

            return new News(id, title, shortText, fullText, creationDate, modificationDate);
        }
    }


}
