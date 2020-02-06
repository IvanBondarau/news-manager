package com.epam.lab.dao;

import com.epam.lab.exception.ResourceNotFoundException;
import com.epam.lab.model.Tag;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class JdbcTagDaoTest {
    private static final String DEFAULT_DATA =
            "INSERT INTO public.author VALUES(1000, 'default name', 'default surname');\n" +
                    "INSERT INTO public.news VALUES(1001, 'news1', 'short text', 'long text', '2000-01-01', '2000-01-02');\n" +
                    "INSERT INTO public.news VALUES(1002, 'news2', 'short text', 'long text', '2000-01-01', '2000-01-02');\n" +
                    "INSERT INTO public.news VALUES(1003, 'news3', 'short text', 'long text', '2000-01-01', '2000-01-02');\n" +
                    "INSERT INTO public.tag VALUES(1001, 'tag1');\n" +
                    "INSERT INTO public.tag VALUES(1002, 'tag2');\n" +
                    "INSERT INTO public.tag VALUES(1003, 'tag3');\n" +
                    "INSERT INTO public.news_tag VALUES(1001, 1002);\n" +
                    "INSERT INTO public.news_tag VALUES(1001, 1003);\n" +
                    "INSERT INTO public.news_tag VALUES(1002, 1001);\n" +
                    "INSERT INTO public.news_tag VALUES(1002, 1002);\n" +
                    "INSERT INTO public.news_tag VALUES(1002, 1003);\n" +
                    "INSERT INTO public.news_tag VALUES(1003, 1001);\n" +
                    "INSERT INTO public.news_tag VALUES(1003, 1003);\n" +
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
        DataSourceHolder dataSourceHolder = new DataSourceHolder();
        dataSourceHolder.setDataSource(embeddedDatabase);
        tagDao = new JdbcTagDao(dataSourceHolder);
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

        assertEquals(4, tags.size());
        assertEquals(tag, tags.get(3));

    }

    @Test(expected = Exception.class)
    public void createNullField() {
        Tag tag = new Tag(null);
        tagDao.create(tag);
    }

    @Test
    public void readShouldBeValid() throws ResourceNotFoundException {
        Tag tag = new Tag("name");

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );

        Tag loaded = tagDao.read(tag.getId());
        assertEquals(tag, loaded);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void readUserNotExist() throws ResourceNotFoundException {
        tagDao.read(11);
    }


    @Test
    public void updateShouldBeValid() throws ResourceNotFoundException {
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

        assertEquals(4, tags.size());
        assertEquals(tag, tags.get(0));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateUserNotExist() throws ResourceNotFoundException {
        Tag tag = new Tag(142, "x");
        tagDao.update(tag);
    }

    @Test(expected = Exception.class)
    public void updateNullField() throws ResourceNotFoundException {
        Tag tag = new Tag(11, "name");

        jdbcTemplate.update("INSERT INTO public.tag VALUES(?, ?)",
                tag.getId(),
                tag.getName()
        );

        tag.setName(null);

        tagDao.update(tag);
    }

    @Test
    public void deleteShouldBeValid() throws ResourceNotFoundException {

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

        assertEquals(3, tags.size());

    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteUserNotExist() throws ResourceNotFoundException {
        tagDao.delete(23);
    }

    @Test
    public void getNewsIdByTagShouldBeValid() throws ResourceNotFoundException {
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


}