package com.epam.lab.dao;

import com.epam.lab.model.Author;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthorJpaDaoTest extends AbstractDatabaseTest {

    @Autowired
    private AuthorDao authorDao;

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

        jdbcTemplate.update("INSERT INTO news_manager.author VALUES(?, ?, ?)", author.getId(), author.getName(), author.getSurname());

        Author loaded = authorDao.read(author.getId());
        assertEquals(author, loaded);
    }

    @Test(expected = Exception.class)

    public void readAuthorNotExist() {
        authorDao.read(11);
    }

    @Test
    @Transactional
    @Rollback
    public void updateShouldBeValid() {

        Author author = new Author(20L, "name", "surname");

        jdbcTemplate.update("INSERT INTO news_manager.author VALUES(?, ?, ?)", author.getId(), author.getName(), author.getSurname());

        author = authorDao.read(20L);
        author.setSurname("new surname");

        authorDao.update(author);


        List<Author> authors = authorDao.getAll();

        assertEquals(2, authors.size());
        assertTrue(authors.stream().anyMatch(authorLoaded -> authorLoaded.getSurname().equals("new surname") && authorLoaded.getId().equals(20L)));
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
        authorDao.delete(1000);

        List<Author> authors = authorDao.getAll();
        assertFalse(authors.stream().map(Author::getId).anyMatch(id -> id.equals(1000L)));
    }

    @Test(expected = Exception.class)
    public void deleteAuthorNotExist() {
        authorDao.delete(23);
    }

    @Test
    @Transactional
    @Rollback
    public void getNewsIdByAuthorShouldBeValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)", 1000, 1000);

        List<Long> ids = authorDao.findNewsByAuthorId(1000);

        assertTrue(ids.contains(1000L));
    }

    @Test
    @Transactional
    @Rollback
    public void getNewsIdByAuthorNameValid() {
        jdbcTemplate.update("INSERT INTO news_manager.news_author(news_id, author_id) VALUES(?, ?)", 1000, 1000);

        List<Long> result = authorDao.findNewsByAuthorName("default name");
        assertEquals(1, result.size());
        assertEquals(Long.valueOf(1000L), result.get(0));
    }

    @Test
    @Transactional
    @Rollback
    public void getNewsIdByAuthorSurnameValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)", 1000, 1000);

        List<Long> result = authorDao.findNewsByAuthorSurname("default surname");
        assertEquals(1, result.size());
    }

    @Test
    public void getAllValid() {
        List<Author> result = authorDao.getAll();
        assertEquals(1, result.size());
    }


}