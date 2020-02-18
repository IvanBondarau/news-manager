package com.epam.lab.dao;

import com.epam.lab.configuration.DaoConfig;
import com.epam.lab.model.Author;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoConfig.class)
public class AuthorHibernateDaoTest {

    private static final Logger logger = Logger.getLogger(AuthorHibernateDaoTest.class);
    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;
    @Autowired
    private AuthorDao authorDao;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

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

        EntityTransaction transaction = entityManager.unwrap(Session.class).beginTransaction();
        entityManager.flush();
        transaction.commit();
    }

    @Test
    public void createShouldBeValid() {

        EntityTransaction transaction = entityManager.unwrap(Session.class).beginTransaction();

        Author author = new Author("name", "surname");
        authorDao.create(author);

        transaction.commit();

        List<Author> authors = jdbcTemplate.query("select * from public.author", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            return new Author(id, name, surname);
        });


        assertEquals(2, authors.size());
        assertEquals(author, authors.get(1));

    }


    @Test
    public void readShouldBeValid() {

        EntityTransaction transaction = entityManager.unwrap(Session.class).beginTransaction();

        Author author = new Author(7, "name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        entityManager.flush();
        transaction.commit();

        Author loaded = authorDao.read(author.getId());
        assertEquals(author, loaded);
    }

    @Test(expected = Exception.class)
    public void readAuthorNotExist() {
        authorDao.read(11);
    }


    @Test
    public void updateShouldBeValid() {

        EntityTransaction transaction = entityManager.unwrap(Session.class).beginTransaction();

        Author author = new Author("name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        author = authorDao.read(0);
        author.setSurname("new surname");

        authorDao.update(author);

        transaction.commit();

        List<Author> authors = jdbcTemplate.query("SELECT * FROM public.author", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            return new Author(id, name, surname);
        });
        logger.info(authors);
        assertEquals(2, authors.size());
        assertEquals(author, authors.get(0));
    }

    @Test(expected = Exception.class)
    public void updateAuthorNotExist() {
        Author author = new Author(11, "x", "x");
        authorDao.update(author);
    }

    @Test(expected = Exception.class)
    public void updateNullField() {

        EntityTransaction transaction = entityManager.unwrap(Session.class).beginTransaction();

        Author author = new Author("name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        author.setName(null);
        author.setSurname("new surname");

        authorDao.update(author);

        transaction.commit();
    }

    @Test
    public void deleteShouldBeValid() {


        EntityTransaction transaction = entityManager.unwrap(Session.class).beginTransaction();

        long authorId = 32;

        Author author = new Author(authorId, "name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        authorDao.delete(authorId);

        transaction.commit();

        List<Author> authors = jdbcTemplate.query("select * from public.author", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            return new Author(id, name, surname);
        });

        assertEquals(1, authors.size());

    }

    @Test(expected = Exception.class)
    public void deleteAuthorNotExist() {
        authorDao.delete(23);
    }

    @Test
    public void getNewsIdByAuthorShouldBeValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000, 1000);

        List<Long> ids = authorDao.getNewsIdByAuthor(1000);

        assertTrue(ids.contains(1000L));
    }

    @Test
    public void getNewsIdByAuthorNameValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000, 1000);

        List<Long> result = authorDao.getNewsIdByAuthorName("default name");
        assertEquals(1, result.size());
    }

    @Test
    public void getNewsIdByAuthorSurnameValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000, 1000);

        List<Long> result = authorDao.getNewsIdByAuthorSurname("default surname");
        assertEquals(1, result.size());
    }

    @Test
    public void getAllValid() {
        List<Author> result = authorDao.getAll();
        assertEquals(1, result.size());
    }


}