package com.epam.lab.dao;

import com.epam.lab.configuration.DaoConfig;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoConfig.class)
public class NewsHibernateDaoTest {
    private static final Logger logger = Logger.getLogger(AuthorHibernateDaoTest.class);
    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;
    @Autowired
    private NewsDao newsDao;
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

        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }

    @Test
    public void createShouldBeValid() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        News news = new News(
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), Date.valueOf("2019-12-13"));

        newsDao.create(news);
        transaction.commit();

        List<News> newsList = jdbcTemplate.query("select * from public.news", (resultSet, i) -> {
                    long id = resultSet.getLong(1);
                    String title = resultSet.getString(2);
                    String shortText = resultSet.getString(3);
                    String fullText = resultSet.getString(4);
                    Date creationDate = resultSet.getDate(5);
                    Date modificationDate = resultSet.getDate(6);

                    return new News(id, title, shortText, fullText, creationDate, modificationDate);
                }
        );

        assertEquals(5, newsList.size());
        assertEquals(news, newsList.get(4));

    }

    @Test(expected = Exception.class)
    public void createNullField() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        News news = new News(
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), null);
        newsDao.create(news);
        transaction.commit();
    }

    @Test
    public void readShouldBeValid() {

        News news = new News(
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
    public void readNewsNotExist()  {
        newsDao.read(11);
    }


    @Test
    public void updateShouldBeValid()  {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        News news = new News(
                1,
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), Date.valueOf("2019-12-13"));

        jdbcTemplate.update(
                "INSERT INTO public.news(id, title, short_text, full_text, creation_date, modification_date) "
                        + "VALUES(?, ?, ?, ?, ?, ?)",
                news.getId(),
                news.getTitle(),
                news.getShortText(),
                news.getFullText(),
                news.getCreationDate(),
                news.getModificationDate()
        );

        news = newsDao.read(1);

        news.setTitle("new title");
        news.setShortText("new text");
        news.setFullText("new ftext");
        news.setCreationDate(Date.valueOf("2020-01-01"));
        news.setModificationDate(Date.valueOf("2020-01-02"));

        newsDao.update(news);

        transaction.commit();

        List<News> newsList = jdbcTemplate.query("SELECT * FROM public.news", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String title = resultSet.getString(2);
            String shortText = resultSet.getString(3);
            String fullText = resultSet.getString(4);
            Date creationDate = resultSet.getDate(5);
            Date modificationDate = resultSet.getDate(6);

            return new News(id, title, shortText, fullText, creationDate, modificationDate);
        });

        assertEquals(5, newsList.size());
        assertEquals(news, newsList.get(0));
    }

    @Test(expected = Exception.class)
    public void updateNewsNotExist() {
        News news = new News(
                20,
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), Date.valueOf("2019-12-13"));
        newsDao.update(news);
    }

    @Test(expected = Exception.class)
    public void updateNullField() {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        News news = new News(
                20,
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

        news = newsDao.read(20);

        news.setCreationDate(null);

        newsDao.update(news);
        transaction.commit();
    }

    @Test
    public void deleteShouldBeValid() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        long newsId = 32;

        News news = new News(
                newsId,
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

        newsDao.delete(newsId);
        transaction.commit();

        List<News> newsList = jdbcTemplate.query("select * from public.news", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String title = resultSet.getString(2);
            String shortText = resultSet.getString(3);
            String fullText = resultSet.getString(4);
            Date creationDate = resultSet.getDate(5);
            Date modificationDate = resultSet.getDate(6);

            return new News(id, title, shortText, fullText, creationDate, modificationDate);
        });

        assertEquals(4, newsList.size());

    }


    @Test
    public void getNewsAuthorValid() {

        News news = newsDao.read(1000);

        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                news.getId(),
                1000);

        long loadedId = newsDao.getAuthorIdByNews(1000);

        assertEquals(1000, loadedId);

    }

    @Test(expected = Exception.class)
    public void getNewsAuthorNotExist() {
        newsDao.getAuthorIdByNews(1000);
    }

    @Test
    public void getNewsTagsValid() {

        jdbcTemplate.update("INSERT INTO news_tag(news_id, tag_id) VALUES(?, ?)",
                1000, 1000);

        List<Long> loadedId = newsDao.getTagsIdForNews(1000);

        assertTrue(loadedId.contains(1000L));

    }

    @Test
    public void setNewsAuthorValid() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        News news = newsDao.read(1000);

        newsDao.setNewsAuthor(1000, 1000);
        transaction.commit();
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM news_author WHERE news_id = ? AND author_id = ?",
                new Object[]{news.getId(), 1000},
                (resultSet, i) -> resultSet.getLong(1));
        assertEquals(Long.valueOf(1), count);
    }

    @Test(expected = Exception.class)
    public void setNewsAuthorDoubleSet() {

        newsDao.setNewsAuthor(1000, 1000);
        newsDao.setNewsAuthor(1000, 1000);

    }

    @Test(expected = RuntimeException.class)
    public void setNewsAuthorInvalidAuthorId() {

        newsDao.setNewsAuthor(1000, 5000);
    }

    @Test
    public void setNewsTagValid()  {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        News news = newsDao.read(1000);

        newsDao.setNewsTag(1000, 1000);
        transaction.commit();

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM news_tag WHERE news_id = ? AND tag_id = ?",
                new Object[]{news.getId(), 1000},
                (resultSet, i) -> resultSet.getLong(1));
        assertEquals(Long.valueOf(1), count);
    }


    @Test(expected = RuntimeException.class)
    public void setNewsAuthorInvalidTagId() {

        newsDao.setNewsTag(1000, 5000);
    }


    @Test
    public void deleteNewsAuthorValid() {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000, 1000);

        newsDao.deleteNewsAuthor(1000);
        transaction.commit();
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM news_author WHERE news_id = ? AND author_id = ?",
                new Object[]{1000, 1000},
                (resultSet, i) -> resultSet.getLong(1));
        assertEquals(Long.valueOf(0), count);
    }

    @Test(expected = Exception.class)
    public void deleteNewsAuthorNotExist() {
        newsDao.deleteNewsAuthor(1000);
    }

    @Test(expected = Exception.class)
    public void deleteNewsNotExist() {
        newsDao.delete(5000);
    }

    @Test
    public void deleteNewsTagValid() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        jdbcTemplate.update("INSERT INTO news_tag(news_id, tag_id) VALUES(?, ?)",
                1000, 1000);

        newsDao.deleteNewsTag(1000, 1000);
        transaction.commit();

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM news_tag WHERE news_id = ? AND tag_id = ?",
                new Object[]{1000, 1000},
                (resultSet, i) -> resultSet.getLong(1));
        assertEquals(Long.valueOf(0), count);
    }

    @Test(expected = Exception.class)
    public void deleteNewsTagNotExist() {
        newsDao.deleteNewsTag(1000, 5000);
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