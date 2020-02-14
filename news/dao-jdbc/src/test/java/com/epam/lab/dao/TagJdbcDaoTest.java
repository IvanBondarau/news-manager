package com.epam.lab.dao;

import com.epam.lab.exception.TagNotFoundException;
import com.epam.lab.model.Tag;
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
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TagJdbcDaoTest {

    private static EmbeddedDatabase embeddedDatabase;
    private TagDao tagDao;
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
        tagDao = new TagJdbcDao(embeddedDatabase);
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

        Tag tag = new Tag("name");
        tagDao.create(tag);

        List<Tag> tags = jdbcTemplate.query("select * from public.tag", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            return new Tag(id, name);
        });

        assertEquals(5, tags.size());
        assertEquals(tag, tags.get(4));

    }

    @Test(expected = Exception.class)
    public void createNullField() {
        Tag tag = new Tag(null);
        tagDao.create(tag);
    }

    @Test
    public void readShouldBeValid() {
        Tag tag = new Tag("name");

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );

        Tag loaded = tagDao.read(tag.getId());
        assertEquals(tag, loaded);
    }

    @Test(expected = TagNotFoundException.class)
    public void readTagNotExist() {
        tagDao.read(11);
    }


    @Test
    public void updateShouldBeValid() {
        Tag tag = new Tag("name");

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );

        tag.setName("new name");

        tagDao.update(tag);

        List<Tag> tags = jdbcTemplate.query("SELECT * FROM public.tag", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            return new Tag(id, name);
        });

        assertEquals(5, tags.size());
        assertEquals(tag, tags.get(0));
    }

    @Test(expected = TagNotFoundException.class)
    public void updateTagNotExist() {
        Tag tag = new Tag(142, "x");
        tagDao.update(tag);
    }

    @Test(expected = Exception.class)
    public void updateNullField() {
        Tag tag = new Tag(11, "name");

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );

        tag.setName(null);

        tagDao.update(tag);
    }

    @Test
    public void deleteShouldBeValid() {

        long tagId = 32;

        Tag tag = new Tag(tagId, "name");

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );

        tagDao.delete(tagId);

        List<Tag> tags = jdbcTemplate.query("select * from public.tag", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            return new Tag(id, name);
        });

        assertEquals(4, tags.size());

    }

    @Test(expected = TagNotFoundException.class)
    public void deleteTagNotExist() {
        tagDao.delete(23);
    }

    @Test
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
    public void findByNameNotFound() {

        Optional<Tag> result = tagDao.findByName("superlongabsentname");

        assertFalse(result.isPresent());
    }

    @Test
    public void findByTagNamesValid() {
        Set<String> tagNames = new HashSet<>();
        tagNames.add("tag1");
        tagNames.add("tag3");

        List<Long> result = tagDao.findNewsIdByTagNames(tagNames);
        assertEquals(2, result.size());
    }

    @Test
    public void getAllValid() {
        List<Tag> result = tagDao.getAll();
        assertEquals(4, result.size());
    }


}