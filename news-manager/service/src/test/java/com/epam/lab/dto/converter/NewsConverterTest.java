package com.epam.lab.dto.converter;

import com.epam.lab.dto.AuthorDto;
import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.TagDto;
import com.epam.lab.exception.InvalidNumberOfAuthorsException;
import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.HashSet;

import static org.junit.Assert.*;

public class NewsConverterTest {

    private NewsConverter newsConverter;

    private NewsDto validDto;
    private News validEntity;

    @Before
    public void init() {
        AuthorConverter authorConverter = new AuthorConverter();
        TagConverter tagConverter = new TagConverter();
        newsConverter = new NewsConverter(authorConverter, tagConverter);
    }

    @Before
    public void prepareData() {
        validDto = new NewsDto();
        validDto.setId(100L);
        validDto.setTitle("Title");
        validDto.setShortText("Short text");
        validDto.setFullText("Full text");
        validDto.setCreationDate(Date.valueOf("2019-02-01"));
        validDto.setModificationDate(Date.valueOf("2019-02-01"));

        validEntity = new News(
                100,
                "Title",
                "Short text", "Full text",
                Date.valueOf("2019-02-01"), Date.valueOf("2019-02-01"));

        Author author = new Author(100L, "Test name", "Test surname");
        HashSet<Author> authors = new HashSet<>();
        authors.add(author);
        validEntity.setAuthors(authors);
        Tag tagEntity1 = new Tag(100L, "Tag 1");
        Tag tagEntity2 = new Tag(100L, "Tag 2");
        Tag tagEntity3 = new Tag(100L, "Tag 3");
        HashSet<Tag> tagHashSet = new HashSet<>();
        tagHashSet.add(tagEntity1);
        tagHashSet.add(tagEntity2);
        tagHashSet.add(tagEntity3);
        validEntity.setTags(tagHashSet);

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(100L);
        authorDto.setName("Test name");
        authorDto.setSurname("Test surname");

        validDto.setAuthor(authorDto);

        TagDto tag1 = new TagDto();
        tag1.setId(100L);
        tag1.setName("Tag 1");
        TagDto tag2 = new TagDto();
        tag2.setId(100L);
        tag2.setName("Tag 2");
        TagDto tag3 = new TagDto();
        tag3.setId(100L);
        tag3.setName("Tag 3");

        validDto.addTag(tag1);
        validDto.addTag(tag2);
        validDto.addTag(tag3);
    }

    @Test
    public void convertToDtoValid() {
        News converted = newsConverter.convertToEntity(validDto);
        assertEquals(validEntity, converted);
    }


    @Test
    public void convertToDtoNullTags() {
        validDto.setTags(null);
        News converted = newsConverter.convertToEntity(validDto);
        assertTrue(converted.getTags().isEmpty());
    }


    @Test
    public void convertToDtoNullAuthor() {
        validDto.setAuthor(null);
        News converted = newsConverter.convertToEntity(validDto);
        assertTrue(converted.getAuthors().isEmpty());
    }

    @Test
    public void convertToEntityValid() {
        NewsDto converted = newsConverter.convertToDto(validEntity);
        assertEquals(validDto, converted);
    }

    @Test
    public void convertToEntityNullTags() {
        validEntity.setTags(null);
        NewsDto converted = newsConverter.convertToDto(validEntity);
        assertTrue(converted.getTags().isEmpty());
    }

    @Test
    public void convertToEntityNullAuthor() {
        validEntity.setAuthors(null);
        NewsDto converted = newsConverter.convertToDto(validEntity);
        assertNull(converted.getAuthor());
    }

    @Test(expected = InvalidNumberOfAuthorsException.class)
    public void convertToEntityInvalidNumOfAuthors() {
        validEntity.getAuthors().add(new Author("New author", "New author"));
        NewsDto converted = newsConverter.convertToDto(validEntity);
        assertNull(converted.getAuthor());
    }

}