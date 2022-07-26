package com.epam.lab.dao;

import com.epam.lab.model.News;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NewsJpaDaoTest extends AbstractDatabaseTest {
    @Autowired
    private NewsDao newsDao;
    @PersistenceContext
    private EntityManager entityManager;

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

        jdbcTemplate.update("INSERT INTO news VALUES(?, ?, ?, ?, ?, ?)",
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
        newsDao.delete(1000);

        List<News> newsList = newsDao.getAll();
        assertFalse(newsList.stream().map(News::getId).anyMatch(id -> id.equals(1000L)));

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