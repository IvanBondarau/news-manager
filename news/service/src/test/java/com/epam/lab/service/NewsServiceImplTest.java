package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.*;
import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.util.*;

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

    private SearchCriteria defaultSearchCriteria;

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

    @Before
    public void defaultSearchCriteria() {
        defaultSearchCriteria = new SearchCriteria();
        defaultSearchCriteria.setAuthorName("Author1");
        defaultSearchCriteria.setTagNames(new HashSet<>(Arrays.asList("Tag 1", "Tag 3")));
        defaultSearchCriteria.setSortParams(Arrays.asList(SortOrder.BY_AUTHOR, SortOrder.BY_DATE, SortOrder.BY_TAGS));
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
        Mockito.when(newsDao.read(100)).thenReturn(defaultEntity);
        Mockito.when(newsDao.getTagsIdForNews(100)).thenReturn(Arrays.asList(6L, 7L));
        Mockito.when(tagDao.read(6L)).thenReturn(defaultTag1);
        Mockito.when(tagDao.read(7L)).thenReturn(defaultTag2);
        Mockito.when(newsDao.getAuthorIdByNews(100)).thenReturn(100L);
        Mockito.when(authorDao.read(100)).thenReturn(defaultAuthor);

        NewsDto newsDto = new NewsDto();
        newsDto.setId(100);
        newsDto.setTitle("Updated");
        newsDto.setShortText("Short");
        newsDto.setFullText("Full text");
        AuthorDto updatedAuthor = new AuthorDto();
        updatedAuthor.setName("new author");
        updatedAuthor.setSurname("new author");

        newsDto.setAuthor(updatedAuthor);

        TagDto updatedTag1 = new TagDto(); updatedTag1.setName("new 1");
        TagDto updatedTag2 = new TagDto(); updatedTag2.setName("Tag 2");
        TagDto updatedTag3 = new TagDto(); updatedTag3.setName("Tag 3");

        newsDto.setTags(new HashSet<>(Arrays.asList(updatedTag1, updatedTag2, updatedTag3)));

        service.update(newsDto);

        Mockito.verify(newsDao).read(100);
        Mockito.verify(authorDao).create(any());
        Mockito.verify(tagDao, times(3)).findByName(any());
        Mockito.verify(tagDao, times(3)).create(any());
    }


    @Test
    public void updateAuthorAlreadyExist() {
        Mockito.when(newsDao.read(100)).thenReturn(defaultEntity);
        Mockito.when(newsDao.getTagsIdForNews(100)).thenReturn(Arrays.asList(6L, 7L));
        Mockito.when(tagDao.read(6L)).thenReturn(defaultTag1);
        Mockito.when(tagDao.read(7L)).thenReturn(defaultTag2);
        Mockito.when(newsDao.getAuthorIdByNews(100)).thenReturn(100L);
        Mockito.when(authorDao.read(100)).thenReturn(defaultAuthor);

        NewsDto newsDto = new NewsDto();
        newsDto.setId(100);
        newsDto.setTitle("Updated");
        newsDto.setShortText("Short");
        newsDto.setFullText("Full text");
        AuthorDto updatedAuthor = new AuthorDto();
        updatedAuthor.setId(100);

        newsDto.setAuthor(updatedAuthor);

        TagDto updatedTag1 = new TagDto(); updatedTag1.setName("new 1");
        TagDto updatedTag2 = new TagDto(); updatedTag2.setName("Tag 2");
        TagDto updatedTag3 = new TagDto(); updatedTag3.setName("Tag 3");

        newsDto.setTags(new HashSet<>(Arrays.asList(updatedTag1, updatedTag2, updatedTag3)));

        service.update(newsDto);

        Mockito.verify(newsDao).read(100);
        Mockito.verify(authorDao, times(2)).read(100);
        Mockito.verify(tagDao, times(3)).findByName(any());
    }

    @Test
    public void searchShouldBeValid() {

        Mockito.when(authorDao.getNewsIdByAuthorName(any())).thenReturn(Arrays.asList(102L, 103L, 104L));
        Mockito.when(tagDao.findNewsIdByTagNames(any())).thenReturn(Arrays.asList(101L, 102L, 103L));
        Mockito.when(newsDao.read(102)).thenReturn(defaultEntity);
        Mockito.when(newsDao.read(103)).thenReturn(defaultEntity);
        Mockito.when(authorDao.read(0)).thenReturn(new Author("A", "B"));
        Mockito.when(authorDao.read(100)).thenReturn(new Author("A", "C"));
        Mockito.when(tagDao.read(102)).thenReturn(defaultTag1);
        Mockito.when(tagDao.read(103)).thenReturn(defaultTag1);

        List<NewsDto> result = service.search(defaultSearchCriteria);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getAuthor().toString().compareTo(result.get(1).getAuthor().toString()) < 1);

    }

    @Test
    public void searchEmptyResult() {

        Mockito.when(authorDao.getNewsIdByAuthorName(any())).thenReturn(Arrays.asList(107L, 108L, 109L));
        Mockito.when(tagDao.findNewsIdByTagNames(any())).thenReturn(Arrays.asList(101L, 102L, 103L));
        Mockito.when(newsDao.read(102)).thenReturn(defaultEntity);
        Mockito.when(newsDao.read(103)).thenReturn(defaultEntity);
        Mockito.when(authorDao.read(0)).thenReturn(new Author("A", "B"));
        Mockito.when(authorDao.read(100)).thenReturn(new Author("A", "C"));
        Mockito.when(tagDao.read(102)).thenReturn(defaultTag1);
        Mockito.when(tagDao.read(103)).thenReturn(defaultTag1);

        List<NewsDto> result = service.search(defaultSearchCriteria);

        assertTrue(result.isEmpty());

    }
    @Test
    public void searchEmptySearchCriteria() {
        Mockito.when(authorDao.getNewsIdByAuthorName(any())).thenReturn(Arrays.asList(107L, 108L, 109L));
        Mockito.when(tagDao.findNewsIdByTagNames(any())).thenReturn(Arrays.asList(101L, 102L, 103L));
        Mockito.when(newsDao.read(102)).thenReturn(defaultEntity);
        Mockito.when(newsDao.read(103)).thenReturn(defaultEntity);
        Mockito.when(authorDao.read(0)).thenReturn(new Author("A", "B"));
        Mockito.when(authorDao.read(100)).thenReturn(new Author("A", "C"));
        Mockito.when(tagDao.read(102)).thenReturn(defaultTag1);
        Mockito.when(tagDao.read(103)).thenReturn(defaultTag1);

        List<NewsDto> result = service.search(new SearchCriteria());

        assertTrue(result.isEmpty());
    }

    @Test
    public void deleteValid() {
        service.delete(100);
        Mockito.verify(newsDao).delete(100);
    }


}