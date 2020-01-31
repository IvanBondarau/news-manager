package com.epam.lab.dao;

import com.epam.lab.exception.TagNotFoundException;
import com.epam.lab.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class JdbcTagDao extends AbstractDao implements TagDao {

    private static final String INSERT_STATEMENT =
            "INSERT INTO public.tag(name) VALUES(?)";

    private static final String SELECT_BY_ID_STATEMENT =
            "SELECT id, name FROM public.tag WHERE id = ?";

    private static final String SELECT_BY_NAME_STATEMENT =
            "SELECT id, name FROM public.tag WHERE name = ?";

    private static final String UPDATE_BY_ID_STATEMENT =
            "UPDATE public.tag " +
                    "SET name = ? " +
                    "WHERE id = ?";

    private static final String DELETE_BY_ID_STATEMENT =
            "DELETE FROM public.tag WHERE id = ?";

    private static final String SELECT_NEWS_ID_FOR_TAG =
            "SELECT news_id FROM news_tag WHERE tag_id = ?";

    public JdbcTagDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long create(Tag entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((Connection connection) -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT,
                            Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, entity.getName());
                    return preparedStatement;
                },
                keyHolder
        );
        long key = (long) keyHolder.getKey();
        entity.setId(key);
        return key;
    }

    @Override
    public Tag read(long id) {
        List<Tag> loadedTags = jdbcTemplate.query(
                SELECT_BY_ID_STATEMENT,
                new Object[]{id},
                new TagMapper()
        );

        if (loadedTags.size() == 0) {
            throw new TagNotFoundException("Tag with id " + id + " not found", id);
        }
        return loadedTags.get(0);
    }

    @Override
    public void update(Tag entity) {

        long numOfUpdated = jdbcTemplate.update(UPDATE_BY_ID_STATEMENT,
                entity.getName(),
                entity.getId());

        if (numOfUpdated != 1) {
            throw new TagNotFoundException("Tag with id " + entity.getId() + " not found", entity.getId());
        }

    }

    @Override
    public void delete(long id) {
        long numOfDeleted = jdbcTemplate.update(DELETE_BY_ID_STATEMENT, id);
        if (numOfDeleted != 1) {
            throw new TagNotFoundException("Tag with id " + id + " not found", id);
        }
    }

    @Override
    public List<Long> getNewsIdForTag(Tag tag) {
        return jdbcTemplate.query(SELECT_NEWS_ID_FOR_TAG,
                new Object[]{tag.getId()},
                (resultSet, i) -> resultSet.getLong(1));
    }

    @Override
    public Optional<Tag> findByName(String name) {
        List<Tag> loadedTags = jdbcTemplate.query(
                SELECT_BY_NAME_STATEMENT,
                new Object[]{name},
                new TagMapper()
        );

        if (loadedTags.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(loadedTags.get(0));
        }
    }

    private static final class TagMapper implements RowMapper<Tag> {
        @Override
        public Tag mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            return new Tag(id, name);
        }
    }
}
