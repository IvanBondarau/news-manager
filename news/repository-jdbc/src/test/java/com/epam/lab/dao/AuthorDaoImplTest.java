package com.epam.lab.dao;

import com.epam.lab.configuration.JdbcConfig;
import com.epam.lab.entity.Author;
import com.epam.lab.exception.AuthorNotFoundException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AuthorDaoImplTest {

    private AuthorDao authorDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() {
        String path = JdbcConfig.class.getResource("/database.properties").getPath();
        HikariConfig hikariConfig = new HikariConfig(path);
        DataSource dataSource = new HikariDataSource(hikariConfig);

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.authorDao = new AuthorDaoImpl(dataSource);

        jdbcTemplate.execute("TRUNCATE TABLE public.author");
    }

    @Test
    public void createShouldBeValid() {

        Author author = new Author("name", "surname");
        authorDao.create(author);

        List<Author> authors = jdbcTemplate.query("select * from public.author", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            return new Author(id, name, surname);
        });

        assertEquals(1, authors.size());
        assertEquals(author, authors.get(0));

    }

    @Test(expected = Exception.class)
    public void createNullField() {
        Author author = new Author("name", null);
        authorDao.create(author);
    }

    @Test
    public void readShouldBeValid() {
        Author author = new Author(7, "name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        Author loaded = authorDao.read(author.getId());
        assertEquals(author, loaded);
    }

    @Test(expected = AuthorNotFoundException.class)
    public void readUserNotExist() {
        authorDao.read(11);
    }


    @Test
    public void updateShouldBeValid() {
        Author author = new Author("name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        author.setName("new name");
        author.setSurname("new surname");

        authorDao.update(author);

        List<Author> authors = jdbcTemplate.query("SELECT * FROM public.author", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            return new Author(id, name, surname);
        });

        assertEquals(1, authors.size());
        assertEquals(author, authors.get(0));
    }

    @Test(expected = AuthorNotFoundException.class)
    public void updateUserNotExist() {
        Author author = new Author(11, "x", "x");
        authorDao.update(author);
    }

    @Test(expected = Exception.class)
    public void updateNullField() {
        Author author = new Author("name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        author.setName(null);
        author.setSurname("new surname");

        authorDao.update(author);
    }

    @Test
    public void deleteShouldBeValid() {

        long authorId = 32;

        Author author = new Author(authorId, "name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        authorDao.delete(authorId);

        List<Author> authors = jdbcTemplate.query("select * from public.author", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            return new Author(id, name, surname);
        });

        assertEquals(0, authors.size());

    }

    @Test(expected = AuthorNotFoundException.class)
    public void deleteUserNotExist() {
        authorDao.delete(23);
    }

    @After
    public void clear() {
        jdbcTemplate.execute("TRUNCATE TABLE public.author");
    }

}