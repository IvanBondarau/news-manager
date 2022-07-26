package com.epam.lab.dao;

import com.epam.lab.model.Tag;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TagJpaDaoTest extends AbstractDatabaseTest {

    private static final Logger logger = Logger.getLogger(AuthorJpaDaoTest.class);
    @Autowired
    private TagDao tagDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value = true)
    public void createShouldBeValid() {

        Tag tag = new Tag("name");
        tagDao.create(tag);

        List<Tag> tags = tagDao.getAll();
        assertEquals(5, tags.size());

    }


    @Test
    @Transactional
    @Rollback(value = true)
    public void readShouldBeValid() {

        Tag tag = new Tag("name");
        tag.setId(20L);

        jdbcTemplate.update("INSERT INTO tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );
        Tag loaded = tagDao.read(tag.getId());
        assertEquals(tag, loaded);
    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback(value = true)
    public void readTagNotExist() {
        tagDao.read(11);
    }


    @Test
    @Transactional
    @Rollback(value = true)
    public void updateShouldBeValid() {
        List<Tag> tags1 = tagDao.getAll();

        assertEquals(4, tags1.size());

        Tag tag = new Tag("name");

        tagDao.create(tag);

        tag.setName("new name");

        tagDao.update(tag);

        List<Tag> tags = tagDao.getAll();

        assertEquals(5, tags.size());
        assertEquals(tag, tags.get(4));
    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback(value = true)
    public void updateTagNotExist() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Tag tag = new Tag(142L, "x");
        tagDao.update(tag);
        transaction.commit();
    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback(value = true)
    public void updateNullField() {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Tag tag = new Tag(11L, "name");

        jdbcTemplate.update("INSERT INTO news_tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );

        tag.setName(null);

        tagDao.update(tag);
        transaction.commit();
    }

    @Test
    @Transactional
    @Rollback(value = true)
    public void deleteShouldBeValid() {
        tagDao.delete(1000);

        List<Tag> tags = jdbcTemplate.query("select * from tag", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            return new Tag(id, name);
        });

        assertEquals(4, tags.size());
    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback(value = true)
    public void deleteTagNotExist() {
        tagDao.delete(23);
    }


    @Test
    public void findByNameValid() {

        Optional<Tag> result = tagDao.findByName("tag1");

        assertTrue(result.isPresent());
        assertEquals(result.get().getId(), Long.valueOf(1001));
    }

    @Test
    @Transactional
    @Rollback(value = true)
    public void findByNameNotFound() {

        Optional<Tag> result = tagDao.findByName("superlongabsentname");

        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    @Rollback(value = true)
    public void findByTagNamesValid() {
        Set<String> tagNames = new HashSet<>();
        tagNames.add("tag1");
        tagNames.add("tag3");

        List<Long> result = tagDao.findNewsIdByTagNames(tagNames);
        assertEquals(2, result.size());
    }

    @Test
    @Transactional
    @Rollback(value = true)
    public void getAllValid() {
        List<Tag> result = tagDao.getAll();
        assertEquals(4, result.size());
    }


}