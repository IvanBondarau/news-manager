package com.epam.lab.dao;

import com.epam.lab.configuration.JdbcConfig;
import com.epam.lab.entity.Tag;
import com.epam.lab.entity.Tag;
import com.epam.lab.exception.TagNotFoundException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TagDaoImplTest {
    private TagDao tagDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() {
        String path = JdbcConfig.class.getResource("/database.properties").getPath();
        HikariConfig hikariConfig = new HikariConfig(path);
        DataSource dataSource = new HikariDataSource(hikariConfig);

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.tagDao = new TagDaoImpl(dataSource);

        jdbcTemplate.execute("TRUNCATE TABLE public.tag");
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

        assertEquals(1, tags.size());
        assertEquals(tag, tags.get(0));

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

        assertEquals(1, tags.size());
        assertEquals(tag, tags.get(0));
    }

    @Test(expected = TagNotFoundException.class)
    public void updateUserNotExist() {
        Tag tag = new Tag(142, "x");
        tagDao.update(tag);
    }

    @Test(expected = Exception.class)
    public void updateNullField() {
        Tag tag = new Tag(11,"name");

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

        assertEquals(0, tags.size());

    }

    @Test(expected = TagNotFoundException.class)
    public void deleteUserNotExist() {
        tagDao.delete(23);
    }

    @After
    public void clear() {
        jdbcTemplate.execute("TRUNCATE TABLE public.tag");
    }
}