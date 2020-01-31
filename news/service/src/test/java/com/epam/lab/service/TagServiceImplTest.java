package com.epam.lab.service;

import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.TagDto;
import com.epam.lab.model.Tag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@RunWith(JUnit4.class)
public class TagServiceImplTest {

    @Mock
    private TagDao tagDao;

    @Mock
    private NewsDao newsDao;

    private TagService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        service = new TagServiceImpl(tagDao, newsDao);
    }

    @Test
    public void createNewTagValid() {

        Mockito.when(tagDao.findByName(any())).thenReturn(Optional.empty());

        TagDto tag = new TagDto();
        tag.setName("Test name");

        Tag resultEntity = new Tag("Test name");

        service.create(tag);

        Mockito.verify(tagDao).findByName("Test name");
        Mockito.verify(tagDao).create(resultEntity);

    }

    @Test(expected = RuntimeException.class)
    public void createTagAlreadyExistValid() {

        Tag resultEntity = new Tag(1, "Test name");

        Mockito.when(tagDao.findByName(any())).thenReturn(Optional.of(resultEntity));

        TagDto tag = new TagDto();
        tag.setName("Test name");

        service.create(tag);

    }

    @Test
    public void readTagValid() {

        Tag tag = new Tag(1, "Test name");

        Mockito.when(tagDao.read(1)).thenReturn(tag);

        TagDto result = service.read(1);

        Mockito.verify(tagDao).read(1);

        assertEquals(Long.valueOf(tag.getId()), Long.valueOf(result.getId()));
        assertEquals(tag.getName(), result.getName());

    }

    @Test
    public void updateTagValid() {

        TagDto tag = new TagDto();
        tag.setId(1);
        tag.setName("New tag name");

        service.update(tag);

        Tag resultEntity = new Tag(1, "New tag name");

        Mockito.verify(tagDao).update(resultEntity);

    }

    @Test
    public void deleteTagValid() {

        Mockito.when(newsDao.getTagsIdForNews(1)).thenReturn(Arrays.asList(3L, 4L));

        service.delete(1);

        Mockito.verify(newsDao).deleteNewsTag(3, 1);
        Mockito.verify(newsDao).deleteNewsTag(4, 1);
        Mockito.verify(tagDao).delete(1);

    }


}