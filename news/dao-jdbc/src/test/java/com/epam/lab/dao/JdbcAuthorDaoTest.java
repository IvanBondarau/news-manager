package com.epam.lab.dao;

import com.epam.lab.exception.ResourceNotFoundException;
import com.epam.lab.model.Author;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class JdbcAuthorDaoTest {

    private static final String DEFAULT_DATA =
            "INSERT INTO public.author VALUES(1000, 'default name', 'default surname');\n" +
                    "INSERT INTO public.news VALUES(1000, 'title', 'short text', 'long text', '2000-01-01', '2000-01-02');\n" +
                    "INSERT INTO public.tag VALUES(1000, 'tag name');\n" +
                    "INSERT INTO public.users VALUES(1000, 'name', 'surname', 'login', 'password');\n" +
                    "INSERT INTO public.roles VALUES(1000, 'default role name');";

    private AuthorDao authorDao;
    private JdbcTemplate jdbcTemplate;

    private static EmbeddedDatabase embeddedDatabase;

    @BeforeClass
    public static void initDatabase() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Before
    public void init() {
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        DataSourceHolder dataSourceHolder = new DataSourceHolder();
        dataSourceHolder.setDataSource(embeddedDatabase);
        authorDao = new JdbcAuthorDao(dataSourceHolder);
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

    @AfterClass
    public static void shutdownDatabase() {
        embeddedDatabase.shutdown();
    }

    @Test
    public void createShouldBeValid() {

        Author author = new Author("name", "surname");
        authorDao.create(author);

        List<Author> authors = jdbcTemplate.query("select * from public.author", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            return new Author(id, name, surname);
        });

        assertEquals(2, authors.size());
        assertEquals(author, authors.get(1));

    }

    @Test(expected = Exception.class)
    public void createNullField() {
        Author author = new Author("name", null);
        authorDao.create(author);
    }

    @Test
    public void readShouldBeValid() throws ResourceNotFoundException {
        Author author = new Author(7, "name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        Author loaded = authorDao.read(author.getId());
        assertEquals(author, loaded);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void readAuthorNotExist() throws ResourceNotFoundException {
        authorDao.read(11);
    }


    @Test
    public void updateShouldBeValid() throws ResourceNotFoundException {
        Author author = new Author("name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        author.setName("new name");
        author.setSurname("new surname");

        authorDao.update(author);

        List<Author> authors = jdbcTemplate.query("SELECT * FROM public.author", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            return new Author(id, name, surname);
        });

        assertEquals(2, authors.size());
        assertEquals(author, authors.get(0));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateUserNotExist() throws ResourceNotFoundException {
        Author author = new Author(11, "x", "x");
        authorDao.update(author);
    }

    @Test(expected = Exception.class)
    public void updateNullField() throws ResourceNotFoundException {
        Author author = new Author("name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        author.setName(null);
        author.setSurname("new surname");

        authorDao.update(author);
    }

    @Test
    public void deleteShouldBeValid() throws ResourceNotFoundException {

        long authorId = 32;

        Author author = new Author(authorId, "name", "surname");

        jdbcTemplate.update("INSERT INTO public.author VALUES(?, ?, ?)",
                author.getId(),
                author.getName(),
                author.getSurname()
        );

        authorDao.delete(authorId);

        List<Author> authors = jdbcTemplate.query("select * from public.author", (resultSet, i) -> {
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String surname = resultSet.getString(3);
            return new Author(id, name, surname);
        });

        assertEquals(1, authors.size());

    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteAuthorNotExist() throws ResourceNotFoundException {
        authorDao.delete(23);
    }

    @Test
    public void getNewsIdByAuthorShouldBeValid() {
        jdbcTemplate.update("INSERT INTO news_author(news_id, author_id) VALUES(?, ?)",
                1000, 1000);

        List<Long> ids = authorDao.getNewsIdByAuthor(1000);

        assertTrue(ids.contains(1000L));
    }


}