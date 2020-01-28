package com.epam.lab.dao;

import com.epam.lab.configuration.JdbcConfig;
import com.epam.lab.entity.User;
import com.epam.lab.exception.UserNotFoundException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class UserDaoTest {

    private UserDao userDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() {
        String path = JdbcConfig.class.getResource("/database.properties").getPath();
        HikariConfig hikariConfig = new HikariConfig(path);
        DataSource dataSource = new HikariDataSource(hikariConfig);

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.userDao = new UserDao(dataSource);

        jdbcTemplate.execute("TRUNCATE TABLE public.\"user\"");
    }

    @Test
    public void createShouldBeValid() {

        User user = new User("name", "surname", "login", "password");
        userDao.create(user);

        List<User> users = jdbcTemplate.query("select * from public.\"user\"", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                long id = resultSet.getLong(1);
                String name = resultSet.getString(2);
                String surname = resultSet.getString(3);
                String login = resultSet.getString(4);
                String password = resultSet.getString(5);
                return new User(id, name, surname, login, password);
            }
        });

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));

    }

    @Test(expected = Exception.class)
    public void createNullField() {
        User user = new User("name", null, "login", "password");
        userDao.create(user);
    }

    @Test
    public void readShouldBeValid() {
        User user = new User(7, "name", "surname", "login", "password");

        jdbcTemplate.update("INSERT INTO public.\"user\" VALUES(?, ?, ?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getLogin(),
                user.getPassword());

        User loaded = userDao.read(user.getId());
        assertEquals(user, loaded);
    }

    @Test(expected = UserNotFoundException.class)
    public void readUserNotExist() {
        User user = userDao.read(11);
    }



    @Test
    public void updateShouldBeValid() {
        User user = new User("name", "surname", "login", "password");

        jdbcTemplate.update("INSERT INTO public.\"user\" VALUES(?, ?, ?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getLogin(),
                user.getPassword());

        user.setName("new name");
        user.setSurname("new surname");
        user.setLogin("new login");
        user.setPassword("new password");

        userDao.update(user);

        List<User> users = jdbcTemplate.query("select * from public.\"user\"", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            String login = resultSet.getString(4);
            String password = resultSet.getString(5);
            return new User(id, name, surname, login, password);
        });

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test(expected = UserNotFoundException.class)
    public void updateUserNotExist() {
        User user = new User(11, "t", "t", "t", "t");
        userDao.update(user);
    }

    @Test(expected = Exception.class)
    public void updateNullField() {
        User user = new User("name", "surname", "login", "password");

        jdbcTemplate.update("INSERT INTO public.\"user\" VALUES(?, ?, ?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getLogin(),
                user.getPassword());

        user.setName("new name");
        user.setSurname(null);
        user.setLogin(null);
        user.setPassword("new password");

        userDao.update(user);
    }

    @Test
    public void deleteShouldBeValid() {

        long userId = 32;

        User user = new User(userId, "name", "surname", "login", "password");

        jdbcTemplate.update("INSERT INTO public.\"user\" VALUES(?, ?, ?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getLogin(),
                user.getPassword());

        userDao.delete(userId);

        List<User> users = jdbcTemplate.query("select * from public.\"user\"", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            String login = resultSet.getString(4);
            String password = resultSet.getString(5);
            return new User(id, name, surname, login, password);
        });

        assertEquals(0, users.size());

    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUserNotExist() {
        userDao.delete(23);
    }

    @After
    public void clear() {
        jdbcTemplate.execute("TRUNCATE TABLE public.\"user\"");
    }
}