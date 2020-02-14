package com.epam.lab.service;

import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.converter.TagConverter;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(JUnit4.class)
public class TagServiceImplTest {

    @Mock
    private TagDao tagDao;
    @Mock
    private NewsDao newsDao;

    private TagConverter tagConverter = new TagConverter();

    private TagService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        service = new TagServiceImpl(tagDao, newsDao, tagConverter);
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

    @Test
    public void saveTagAlreadyExistValid() {
        Tag tag = new Tag(70, "Tag 1");
        Mockito.when(tagDao.findByName("Tag 1")).thenReturn(Optional.of(tag));

        TagDto tagDto = new TagDto("Tag 1");

        service.upload(tagDto);

        assertEquals(70, tagDto.getId());
        Mockito.verify(tagDao).findByName("Tag 1");
    }

    @Test
    public void saveTagNewTagValid() {
        Mockito.when(tagDao.findByName("Tag 1")).thenReturn(Optional.empty());

        TagDto tagDto = new TagDto("Tag 1");

        service.upload(tagDto);

        Mockito.verify(tagDao).create(new Tag("Tag 1"));
    }

    @Test
    public void getAllValid() {
        service.getAll();
        Mockito.verify(tagDao).getAll();
    }


}