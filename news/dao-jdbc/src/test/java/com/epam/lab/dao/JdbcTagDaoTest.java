package com.epam.lab.dao;

import com.epam.lab.dao.JdbcTagDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.exception.TagNotFoundException;
import com.epam.lab.model.Tag;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class JdbcTagDaoTest {
    private static final String DEFAULT_DATA =
            "INSERT INTO public.author VALUES(1000, 'default name', 'default surname');\n" +
                    "INSERT INTO public.news VALUES(1000, 'title', 'short text', 'long text', '2000-01-01', '2000-01-02');\n" +
                    "INSERT INTO public.tag VALUES(1000, 'tag name');\n" +
                    "INSERT INTO public.users VALUES(1000, 'name', 'surname', 'login', 'password');\n" +
                    "INSERT INTO public.roles VALUES(1000, 'default role name');";
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
    public void init() {
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        tagDao = new JdbcTagDao(embeddedDatabase);
        jdbcTemplate.execute(DEFAULT_DATA);
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

        assertEquals(2, tags.size());
        assertEquals(tag, tags.get(1));

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
    public void readUserNotExist() {
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

        assertEquals(2, tags.size());
        assertEquals(tag, tags.get(0));
    }

    @Test(expected = TagNotFoundException.class)
    public void updateUserNotExist() {
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

        assertEquals(1, tags.size());

    }

    @Test(expected = TagNotFoundException.class)
    public void deleteUserNotExist() {
        tagDao.delete(23);
    }

    @Test
    public void getNewsIdByTagShouldBeValid() {
        Tag tag = tagDao.read(1000);

        jdbcTemplate.update("INSERT INTO news_tag(news_id, tag_id) VALUES(?, ?)",
                1000, 1000);

        List<Long> ids = tagDao.getNewsIdForTag(tag);

        assertTrue(ids.contains(1000L));
    }

}