package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.AuthorDto;
import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.TagDto;
import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@RunWith(JUnit4.class)
public class NewsServiceImplTest {
    @Mock
    private NewsDao newsDao;
    @Mock
    private TagDao tagDao;
    @Mock
    private AuthorDao authorDao;

    private NewsService service;

    private NewsDto defaultDto;
    private News defaultEntity;
    private Author defaultAuthor;
    private Tag defaultTag1;
    private Tag defaultTag2;
    private Tag defaultTag3;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        service = new NewsServiceImpl(newsDao, authorDao, tagDao);
    }

    @Before
    public void setDefaultDto() {
        defaultDto = new NewsDto();
        defaultDto.setId(100);
        defaultDto.setTitle("Title");
        defaultDto.setShortText("Short text");
        defaultDto.setFullText("Full text");
        defaultDto.setCreationDate(Date.valueOf("2019-02-01"));
        defaultDto.setModificationDate(Date.valueOf("2019-02-01"));

        defaultEntity = new News(
                100,
                "Title",
                "Short text", "Full text",
                Date.valueOf("2019-02-01"), Date.valueOf("2019-02-01"));

        defaultAuthor = new Author(100, "Test name", "Test surname");

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(100);
        authorDto.setName("Test name");
        authorDto.setSurname("Test surname");

        defaultDto.setAuthor(authorDto);

        TagDto tag1 = new TagDto();
        tag1.setName("Tag 1");
        TagDto tag2 = new TagDto();
        tag2.setName("Tag 2");
        TagDto tag3 = new TagDto();
        tag3.setName("Tag 3");

        defaultDto.addTag(tag1);
        defaultDto.addTag(tag2);
        defaultDto.addTag(tag3);

        defaultTag1 = new Tag(6, "Tag 1");
        defaultTag2 = new Tag(7, "Tag 2");
        defaultTag3 = new Tag(8, "Tag 3");
    }

    @Test
    public void createValid() {
        Mockito.when(tagDao.findByName("Tag 2")).thenReturn(Optional.of(new Tag(2, "Tag 2")));
        Mockito.when(tagDao.findByName("Tag 1")).thenReturn(Optional.empty());
        Mockito.when(tagDao.findByName("Tag 3")).thenReturn(Optional.empty());

        Mockito.when(authorDao.read(100)).thenReturn(new Author(100, "Name", "Surname"));

        service.create(defaultDto);

        Mockito.verify(tagDao, times(2)).create(any());
        Mockito.verify(newsDao, times(1)).create(any());
        Mockito.verify(tagDao, times(3)).findByName(any());

    }

    @Test
    public void readValid() {
        Mockito.when(newsDao.getTagsIdForNews(100)).thenReturn(Arrays.asList(6L, 7L, 8L));
        Mockito.when(newsDao.read(100)).thenReturn(defaultEntity);
        Mockito.when(tagDao.read(6)).thenReturn(defaultTag1);
        Mockito.when(tagDao.read(7)).thenReturn(defaultTag2);
        Mockito.when(tagDao.read(8)).thenReturn(defaultTag3);
        Mockito.when(newsDao.getAuthorIdByNews(100)).thenReturn(100L);
        Mockito.when(authorDao.read(100)).thenReturn(defaultAuthor);

        NewsDto result = service.read(100);
        assertEquals(defaultDto, result);

    }

    @Test
    public void updateValid() {


        Mockito.when(newsDao.getTagsIdForNews(100)).thenReturn(Arrays.asList(6L, 7L, 8L));
        Mockito.when(newsDao.read(100)).thenReturn(defaultEntity);
        Mockito.when(tagDao.read(6)).thenReturn(defaultTag1);
        Mockito.when(tagDao.read(7)).thenReturn(defaultTag2);
        Mockito.when(tagDao.read(8)).thenReturn(defaultTag3);
        Mockito.when(newsDao.getAuthorIdByNews(100)).thenReturn(100L);
        Mockito.when(authorDao.read(100)).thenReturn(defaultAuthor);

        NewsDto updatedDto = new NewsDto();
        updatedDto.setId(100);
        updatedDto.setTitle("New Title");
        updatedDto.setShortText("New Short text");
        updatedDto.setFullText("New Full text");
        updatedDto.setCreationDate(Date.valueOf("2019-02-03"));
        updatedDto.setModificationDate(Date.valueOf("2019-02-03"));

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(100);
        authorDto.setName("Test name");
        authorDto.setSurname("Test surname");

        updatedDto.setAuthor(authorDto);

        TagDto tag1 = new TagDto();
        tag1.setName("Tag 1");
        TagDto tag3 = new TagDto();
        tag3.setName("Tag 3");
        TagDto tag4 = new TagDto();
        tag4.setId(10);
        tag4.setName("Tag 4");

        updatedDto.addTag(tag1);
        updatedDto.addTag(tag3);
        updatedDto.addTag(tag4);

        service.update(updatedDto);

        Mockito.verify(newsDao).update(any());

    }

    @Test
    public void deleteValid() {
        service.delete(100);
        Mockito.verify(newsDao).delete(100);
    }
}