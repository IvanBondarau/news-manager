package com.epam.lab.dao;

import com.epam.lab.exception.UserNotFoundException;
import com.epam.lab.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class JdbcUserDao extends AbstractDao implements UserDao {

    private static final String INSERT_STATEMENT =
            "INSERT INTO public.users(name, surname, login, password) VALUES(?, ?, ?, ?)";

    private static final String SELECT_BY_ID_STATEMENT =
            "SELECT id, name, surname, login, password FROM public.users WHERE id = ?";

    private static final String UPDATE_BY_ID_STATEMENT =
            "UPDATE public.users " +
                    "SET name = ?, surname = ?, login = ?, password = ? " +
                    "WHERE id = ?";

    private static final String DELETE_BY_ID_STATEMENT =
            "DELETE FROM public.users WHERE id = ?";


    @Autowired
    public JdbcUserDao(DataSourceHolder dataSourceHolder) {
        setDataSource(dataSourceHolder.getDataSource());
    }

    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public long create(User entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((Connection connection) -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT,
                            Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, entity.getName());
                    preparedStatement.setString(2, entity.getSurname());
                    preparedStatement.setString(3, entity.getLogin());
                    preparedStatement.setString(4, entity.getPassword());
                    return preparedStatement;
                },
                keyHolder
        );
        long key = (long) keyHolder.getKey();
        entity.setId(key);
        return key;
    }

    @Override
    public User read(long id) {
        List<User> loadedUsers = jdbcTemplate.query(SELECT_BY_ID_STATEMENT,
                new Object[]{id},
                new UserRowMapper());

        if (loadedUsers.size() == 0) {
            throw new UserNotFoundException(id);
        }
        return loadedUsers.get(0);
    }

    @Override
    public void update(User entity) {

        long numOfUpdated = jdbcTemplate.update(UPDATE_BY_ID_STATEMENT,
                entity.getName(),
                entity.getSurname(),
                entity.getLogin(),
                entity.getPassword(),
                entity.getId());

        if (numOfUpdated != 1) {
            throw new UserNotFoundException(entity.getId());
        }
    }

    @Override
    public void delete(long id) {
        long numOfDeleted = jdbcTemplate.update(DELETE_BY_ID_STATEMENT, id);
        if (numOfDeleted != 1) {
            throw new UserNotFoundException(id);
        }
    }

    private static final class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {

            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            String login = resultSet.getString(4);
            String password = resultSet.getString(5);

            return new User(id, name, surname, login, password);
        }
    }
}
