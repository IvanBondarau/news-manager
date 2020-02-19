package com.epam.lab.dao;

import com.epam.lab.configuration.DaoConfig;
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
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoConfig.class)
public class TagHibernateDaoTest {

    private static final Logger logger = Logger.getLogger(AuthorHibernateDaoTest.class);
    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;
    @Autowired
    private TagDao tagDao;
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
    @Transactional
    @Rollback(value = true)
    public void createShouldBeValid() {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Tag tag = new Tag("name");
        tagDao.create(tag);
        transaction.commit();

        List<Tag> tags = jdbcTemplate.query("select * from public.tag", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            return new Tag(id, name);
        });

        assertEquals(5, tags.size());
        assertEquals(tag, tags.get(4));

    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback(value = true)
    public void createNullField() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Tag tag = new Tag(null);
        tagDao.create(tag);
        transaction.commit();
    }

    @Test
    @Transactional
    @Rollback(value = true)
    public void readShouldBeValid() {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Tag tag = new Tag("name");
        tag.setId(20);

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );
        transaction.commit();
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

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Tag tag = new Tag("name");

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );

        tag.setName("new name");

        tagDao.update(tag);
        transaction.commit();

        List<Tag> tags = jdbcTemplate.query("SELECT * FROM public.tag", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            return new Tag(id, name);
        });

        assertEquals(5, tags.size());
        assertEquals(tag, tags.get(0));
    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback(value = true)
    public void updateTagNotExist() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Tag tag = new Tag(142, "x");
        tagDao.update(tag);
        transaction.commit();
    }

    @Test(expected = Exception.class)
    @Transactional
    @Rollback(value = true)
    public void updateNullField() {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Tag tag = new Tag(11, "name");

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
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

        long tagId = 32;

        Tag tag = new Tag(tagId, "name");

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );

        EntityTransaction transaction = entityManager.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        tagDao.delete(tagId);
        transaction.commit();

        List<Tag> tags = jdbcTemplate.query("select * from public.tag", (resultSet, i) -> {
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
    @Transactional
    @Rollback(value = true)
    public void getNewsIdByTagShouldBeValid() {
        Tag tag = tagDao.read(1001);

        jdbcTemplate.update("INSERT INTO news_tag(news_id, tag_id) VALUES(?, ?)",
                1001, 1001);

        List<Long> ids = tagDao.getNewsIdByTag(tag);

        assertTrue(ids.contains(1001L));
    }

    @Test
    public void findByNameValid() {

        Optional<Tag> result = tagDao.findByName("tag1");

        assertTrue(result.isPresent());
        assertEquals(Long.valueOf(result.get().getId()), Long.valueOf(1001));
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