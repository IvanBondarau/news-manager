package com.epam.lab.dao;

import com.epam.lab.entity.Tag;
import com.epam.lab.exception.TagNotFoundException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class TagDaoImpl extends AbstractDao implements TagDao {

    private static final String INSERT_STATEMENT =
            "INSERT INTO public.tag(name) VALUES(?)";

    private static final String SELECT_BY_ID_STATEMENT =
            "SELECT id, name FROM public.tag WHERE id = ?";

    private static final String UPDATE_BY_ID_STATEMENT =
            "UPDATE public.tag " +
                    "SET name = ? " +
                    "WHERE id = ?";

    private static final String DELETE_BY_ID_STATEMENT =
            "DELETE FROM public.tag WHERE id = ?";

    public TagDaoImpl(DataSource dataSource) {
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

    private static final class TagMapper implements RowMapper<Tag> {
        @Override
        public Tag mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            return new Tag(id, name);
        }
    }
}
