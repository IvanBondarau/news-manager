package com.epam.lab.service;

import com.epam.lab.converter.AuthorConverter;
import com.epam.lab.converter.TagConverter;
import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.*;
import com.epam.lab.converter.NewsConverter;
import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    @Mock
    private TagService tagService;
    @Mock
    private AuthorService authorService;

    private NewsDto defaultDto;
    private News defaultEntity;
    private Tag defaultTag1;

    private FilterCriteria defaultFilterCriteria;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        AuthorConverter authorConverter = new AuthorConverter();
        TagConverter tagConverter = new TagConverter();
        NewsConverter newsConverter = new NewsConverter(authorConverter, tagConverter);
        service = new NewsServiceImpl(newsDao, authorDao, tagDao, tagService, authorService, newsConverter);
    }

    @Before
    public void setDefaultDto() {
        defaultDto = new NewsDto();
        defaultDto.setId(100L);
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

        Author defaultAuthor = new Author(100L, "Test name", "Test surname");

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(100L);
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

        defaultTag1 = new Tag(6L, "Tag 1");
        Tag defaultTag2 = new Tag(7L, "Tag 2");
        Tag defaultTag3 = new Tag(8L, "Tag 3");
    }

    @Before
    public void defaultSearchCriteria() {
        defaultFilterCriteria = new FilterCriteria();
        defaultFilterCriteria.setAuthorName("Author1");
        defaultFilterCriteria.setTagNames(new HashSet<>(Arrays.asList("Tag 1", "Tag 3")));
        defaultFilterCriteria.setSortParams(Arrays.asList(SortOrder.BY_AUTHOR, SortOrder.BY_DATE, SortOrder.BY_TAGS));
    }

    @Test
    @Ignore
    public void createValid() {

        service.create(defaultDto);

        Mockito.verify(tagService, times(3)).upload(any());
        Mockito.verify(newsDao, times(1)).create(any());

    }

    @Test
    public void readValid() {
        Mockito.when(newsDao.getTagsIdForNews(100)).thenReturn(Arrays.asList(6L, 7L, 8L));
        Mockito.when(newsDao.getAuthorIdByNews(100)).thenReturn(100L);
        Mockito.when(newsDao.read(100)).thenReturn(defaultEntity);

        NewsDto result = service.read(100);

        Mockito.verify(newsDao).read(100);
    }

    @Test
    public void updateValid() {
        Mockito.when(newsDao.read(100)).thenReturn(defaultEntity);
        Mockito.when(newsDao.getTagsIdForNews(100)).thenReturn(Arrays.asList(6L, 7L));
        Mockito.when(newsDao.getAuthorIdByNews(100)).thenReturn(100L);
        Mockito.when(tagService.read(6)).thenReturn(new TagDto("Tag 2"));
        Mockito.when(tagService.read(7)).thenReturn(new TagDto("Tag 3"));
        Mockito.when(authorService.read(100)).thenReturn(new AuthorDto(6L, "default", "default"));

        NewsDto newsDto = new NewsDto();
        newsDto.setId(100L);
        newsDto.setTitle("Updated");
        newsDto.setShortText("Short");
        newsDto.setFullText("Full text");
        AuthorDto updatedAuthor = new AuthorDto();
        updatedAuthor.setId(400L);
        updatedAuthor.setName("new author");
        updatedAuthor.setSurname("new author");

        newsDto.setAuthor(updatedAuthor);

        TagDto updatedTag1 = new TagDto();
        updatedTag1.setName("new 1");
        updatedTag1.setId(400L);
        TagDto updatedTag2 = new TagDto();
        updatedTag2.setName("Tag 2");
        TagDto updatedTag3 = new TagDto();
        updatedTag3.setName("Tag 3");

        newsDto.setTags(new HashSet<>(Arrays.asList(updatedTag1, updatedTag2, updatedTag3)));

        service.update(newsDto);

        Mockito.verify(authorService).upload(any());
        Mockito.verify(tagService, times(3)).upload(any());
        Mockito.verify(newsDao).update(any());
    }

    @Test
    public void searchShouldBeValid() {

        Mockito.when(authorDao.getNewsIdByAuthorName(any())).thenReturn(Arrays.asList(102L, 103L, 104L));
        Mockito.when(tagDao.findNewsIdByTagNames(any())).thenReturn(Arrays.asList(101L, 102L, 103L));
        Mockito.when(newsDao.read(102)).thenReturn(defaultEntity);
        Mockito.when(newsDao.read(103)).thenReturn(defaultEntity);
        Mockito.when(newsDao.read(104)).thenReturn(defaultEntity);
        Mockito.when(tagDao.read(102)).thenReturn(defaultTag1);
        Mockito.when(tagDao.read(103)).thenReturn(defaultTag1);

        List<NewsDto> result = service.filter(defaultFilterCriteria);

        assertEquals(2, result.size());

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

        List<NewsDto> result = service.filter(defaultFilterCriteria);

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

        List<NewsDto> result = service.filter(new FilterCriteria());

        assertTrue(result.isEmpty());
    }

    @Test
    public void deleteValid() {
        service.delete(100);
        Mockito.verify(newsDao).delete(100);
    }

    @Test
    public void getAllValid() {
        service.getAll();
        Mockito.verify(newsDao).getAll();
    }

    @Test
    public void countValid() {
        service.count();
        Mockito.verify(newsDao).count();
    }

}