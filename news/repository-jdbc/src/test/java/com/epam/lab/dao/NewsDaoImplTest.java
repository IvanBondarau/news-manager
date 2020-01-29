package com.epam.lab.dao;

import com.epam.lab.configuration.JdbcConfig;
import com.epam.lab.entity.News;
import com.epam.lab.exception.NewsNotFoundException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class NewsDaoImplTest {
    private NewsDao newsDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() {
        String path = JdbcConfig.class.getResource("/database.properties").getPath();
        HikariConfig hikariConfig = new HikariConfig(path);
        DataSource dataSource = new HikariDataSource(hikariConfig);

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.newsDao = new NewsDaoImpl(dataSource);

        jdbcTemplate.execute("TRUNCATE TABLE public.news");
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

        assertEquals(1, newsList.size());
        assertEquals(news, newsList.get(0));

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
    public void readNewsNotExist() {
        newsDao.read(11);
    }


    @Test
    public void updateShouldBeValid() {
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

        assertEquals(1, newsList.size());
        assertEquals(news, newsList.get(0));
    }

    @Test(expected = NewsNotFoundException.class)
    public void updateUserNotExist() {
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

        assertEquals(0, newsList.size());

    }

    @Test(expected = NewsNotFoundException.class)
    public void deleteUserNotExist() {
        newsDao.delete(23);
    }

    @After
    public void clear() {
        jdbcTemplate.execute("TRUNCATE TABLE public.news");
    }
}