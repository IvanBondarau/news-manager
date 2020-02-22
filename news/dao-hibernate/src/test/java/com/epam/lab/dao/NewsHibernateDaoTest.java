package com.epam.lab.dao;

import com.epam.lab.configuration.DaoConfig;
import com.epam.lab.model.News;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoConfig.class)
public class NewsHibernateDaoTest {
    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;
    @Autowired
    private NewsDao newsDao;
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
    @Rollback(value = true)
    public void createShouldBeValid() {
        News news = new News(
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), Date.valueOf("2019-12-13"));

        newsDao.create(news);

        List<News> newsList = newsDao.getAll();

        assertEquals(5, newsList.size());
        assertEquals(news, newsList.get(4));

    }


    @Test
    @Transactional
    @Rollback(value = true)
    public void readShouldBeValid() {

        News news = new News(
                40L,
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), Date.valueOf("2019-12-13"));

        jdbcTemplate.update("INSERT INTO public.news VALUES(?, ?, ?, ?, ?, ?)",
                news.getId(),
                news.getTitle(),
                news.getShortText(),
                news.getFullText(),
                news.getCreationDate(),
                news.getModificationDate()
        );

        News loaded = newsDao.read(news.getId());
        assertEquals(news, loaded);
    }

    @Test(expected = Exception.class)
    public void readNewsNotExist() {
        newsDao.read(11);
    }


    @Test
    @Transactional
    @Rollback
    public void updateShouldBeValid() {

        News news = new News(
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), Date.valueOf("2019-12-13"));

        newsDao.create(news);

        news.setTitle("new title");
        news.setShortText("new text");
        news.setFullText("new ftext");
        news.setCreationDate(Date.valueOf("2020-01-01"));
        news.setModificationDate(Date.valueOf("2020-01-02"));

        newsDao.update(news);


        List<News> newsList = newsDao.getAll();

        assertEquals(5, newsList.size());
        assertEquals(news, newsList.get(4));
    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback
    public void updateNewsNotExist() {
        News news = new News(
                20,
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), Date.valueOf("2019-12-13"));
        newsDao.update(news);
    }


    @Test
    @Transactional
    @Rollback
    public void deleteShouldBeValid() {

        News news = new News(
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), Date.valueOf("2019-12-13"));

        newsDao.create(news);

        List<News> newsList = newsDao.getAll();
        assertTrue(newsList.contains(news));

        newsDao.delete(news.getId());

        newsList = newsDao.getAll();
        assertFalse(newsList.contains(newsDao));

    }


    @Test
    @Transactional
    @Rollback
    public void getNewsAuthorValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000,
                1000);

        entityManager.flush();

        long loadedId = newsDao.getAuthorIdByNewsId(1000);

        assertEquals(1000, loadedId);

    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback
    public void getNewsAuthorNotExist() {
        newsDao.getAuthorIdByNewsId(1000);
    }


    @Test(expected = Exception.class)
    public void deleteNewsNotExist() {
        newsDao.delete(5000);
    }


    @Test
    public void getAllValid() {
        List<News> result = newsDao.getAll();
        assertEquals(4, result.size());
    }


    @Test
    public void countValid() {
        long result = newsDao.count();
        assertEquals(4, result);
    }

}