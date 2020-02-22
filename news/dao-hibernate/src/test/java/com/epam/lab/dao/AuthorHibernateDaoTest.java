package com.epam.lab.dao;

import com.epam.lab.configuration.DaoConfig;
import com.epam.lab.model.Author;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoConfig.class)
public class AuthorHibernateDaoTest {

    private static final Logger logger = Logger.getLogger(AuthorHibernateDaoTest.class);
    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;
    @Autowired
    private AuthorDao authorDao;
    @PersistenceContext
    private EntityManager entityManager;


    @BeforeClass
    public static void initDatabase() {
        HikariConfig config = new HikariConfig("/database.properties");
        dataSource = new HikariDataSource(config);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @AfterClass
    public static void shutdownDatabase() {

    }

    @Before
    public void init() throws SQLException {
        jdbcTemplate = new JdbcTemplate(dataSource);

        Connection connection = dataSource.getConnection();
        ScriptUtils.executeSqlScript(connection, new ClassPathResource("test_data.sql"));
        connection.close();
    }

    @After
    public void clear() {
        jdbcTemplate.update("DELETE FROM roles");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM news_tag");
        jdbcTemplate.update("DELETE FROM news_author");
        jdbcTemplate.update("DELETE FROM news");
        jdbcTemplate.update("DELETE FROM tag");
        jdbcTemplate.update("DELETE FROM author");

    }

    @Test
    @Transactional
    @Rollback
    public void createShouldBeValid() {


        Author author = new Author("name", "surname");
        authorDao.create(author);


        List<Author> authors = authorDao.getAll();

        assertEquals(2, authors.size());
        assertEquals(author, authors.get(1));

    }


    @Test
    @Transactional
    @Rollback
    public void readShouldBeValid() {

        Author author = new Author(7L, "name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );


        Author loaded = authorDao.read(author.getId());
        assertEquals(author, loaded);
    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback
    public void readAuthorNotExist() {
        authorDao.read(11);
    }

    @Test
    public void updateShouldBeValid() {


        Author author = new Author(20L, "name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        author = authorDao.read(20L);
        author.setSurname("new surname");

        authorDao.update(author);


        List<Author> authors = authorDao.getAll();

        assertEquals(2, authors.size());
        assertEquals(author, authors.get(0));
    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback
    public void updateAuthorNotExist() {
        Author author = new Author(11L, "x", "x");
        authorDao.update(author);
    }


    @Test
    @Transactional
    @Rollback
    public void deleteShouldBeValid() {


        Author author = new Author("name", "surname");

        authorDao.create(author);

        List<Author> authors = authorDao.getAll();
        assertTrue(authors.contains(author));

        authorDao.delete(author.getId());


        authors = authorDao.getAll();

        assertFalse(authors.contains(author));

    }

    @Test(expected = Exception.class)
    public void deleteAuthorNotExist() {
        authorDao.delete(23);
    }

    @Test
    @Transactional
    @Rollback
    public void getNewsIdByAuthorShouldBeValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000, 1000);

        List<Long> ids = authorDao.findNewsByAuthorId(1000);

        assertTrue(ids.contains(1000L));
    }

    @Test
    @Transactional
    @Rollback
    public void getNewsIdByAuthorNameValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000, 1000);

        List<Long> result = authorDao.findNewsByAuthorName("default name");
        assertEquals(1, result.size());
        assertEquals(Long.valueOf(1000L), result.get(0));
    }

    @Test
    @Transactional
    @Rollback
    public void getNewsIdByAuthorSurnameValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000, 1000);

        List<Long> result = authorDao.findNewsByAuthorSurname("default surname");
        assertEquals(1, result.size());
    }

    @Test
    public void getAllValid() {
        List<Author> result = authorDao.getAll();
        assertEquals(1, result.size());
    }


}