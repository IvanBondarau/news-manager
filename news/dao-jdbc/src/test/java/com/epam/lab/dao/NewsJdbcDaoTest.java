package com.epam.lab.dao;

import com.epam.lab.exception.*;
import com.epam.lab.model.News;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class NewsJdbcDaoTest {

    private static EmbeddedDatabase embeddedDatabase;
    private NewsDao newsDao;
    private JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void initDatabase() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @AfterClass
    public static void shutdownDatabase() {
        embeddedDatabase.shutdown();
    }

    @Before
    public void init() throws SQLException {
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        newsDao = new NewsJdbcDao(embeddedDatabase);
        Connection connection = embeddedDatabase.getConnection();
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
    public void createShouldBeValid() {

        News news = new News(
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), Date.valueOf("2019-12-13"));

        newsDao.create(news);

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

        News news = new News(
                "title",
                "text", "textx",
                Date.valueOf("2019-12-12"), null);
        newsDao.create(news);
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

    @Test(expected = NewsNotFoundException.class)
    public void readNewsNotExist()  {
        newsDao.read(11);
    }


    @Test
    public void updateShouldBeValid()  {
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

        news.setTitle("new title");
        news.setShortText("new text");
        news.setFullText("new ftext");
        news.setCreationDate(Date.valueOf("2020-01-01"));
        news.setModificationDate(Date.valueOf("2020-01-02"));

        newsDao.update(news);

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

    @Test(expected = NewsNotFoundException.class)
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

        news.setCreationDate(null);

        newsDao.update(news);
    }

    @Test
    public void deleteShouldBeValid() {

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

        News news = newsDao.read(1000);

        newsDao.setNewsAuthor(1000, 1000);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM news_author WHERE news_id = ? AND author_id = ?",
                new Object[]{news.getId(), 1000},
                (resultSet, i) -> resultSet.getLong(1));
        assertEquals(Long.valueOf(1), count);
    }

    @Test(expected = NewsAuthorAlreadySetException.class)
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

        News news = newsDao.read(1000);

        newsDao.setNewsTag(1000, 1000);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM news_tag WHERE news_id = ? AND tag_id = ?",
                new Object[]{news.getId(), 1000},
                (resultSet, i) -> resultSet.getLong(1));
        assertEquals(Long.valueOf(1), count);
    }

    @Test(expected = NewsTagAlreadySetException.class)
    public void setNewsTagDoubleSet() {
        newsDao.setNewsTag(1000, 1000);
        newsDao.setNewsTag(1000, 1000);

    }

    @Test(expected = RuntimeException.class)
    public void setNewsAuthorInvalidTagId() {

        newsDao.setNewsTag(1000, 5000);
    }


    @Test
    public void deleteNewsAuthorValid() {


        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000, 1000);

        newsDao.deleteNewsAuthor(1000);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM news_author WHERE news_id = ? AND author_id = ?",
                new Object[]{1000, 1000},
                (resultSet, i) -> resultSet.getLong(1));
        assertEquals(Long.valueOf(0), count);
    }

    @Test(expected = NewsAuthorNotFoundException.class)
    public void deleteNewsAuthorNotExist() {
        newsDao.deleteNewsAuthor(1000);
    }

    @Test(expected = NewsNotFoundException.class)
    public void deleteNewsNotExist() {
        newsDao.delete(5000);
    }

    @Test
    public void deleteNewsTagValid() {
        jdbcTemplate.update("INSERT INTO news_tag(news_id, tag_id) VALUES(?, ?)",
                1000, 1000);

        newsDao.deleteNewsTag(1000, 1000);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM news_tag WHERE news_id = ? AND tag_id = ?",
                new Object[]{1000, 1000},
                (resultSet, i) -> resultSet.getLong(1));
        assertEquals(Long.valueOf(0), count);
    }

    @Test(expected = NewsTagNotFoundException.class)
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