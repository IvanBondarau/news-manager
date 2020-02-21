package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.converter.AuthorConverter;
import com.epam.lab.dto.AuthorDto;
import com.epam.lab.model.Author;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(JUnit4.class)
public class AuthorServiceImplTest {

    @Mock
    private AuthorDao authorDao;
    @Mock
    private NewsDao newsDao;

    private AuthorConverter converter = new AuthorConverter();

    private AuthorService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        service = new AuthorServiceImpl(authorDao, newsDao, converter);
    }

    @Test
    public void createNewAuthorValid() {

        AuthorDto authorDto = new AuthorDto();
        authorDto.setName("Test name");
        authorDto.setSurname("Test surname");

        Author resultEntity = new Author("Test name", "Test surname");

        service.create(authorDto);

        Mockito.verify(authorDao).create(resultEntity);

    }


    @Test
    public void readAuthorValid() {

        Author author = new Author(1L, "Test name", "Test surname");

        Mockito.when(authorDao.read(1)).thenReturn(author);

        AuthorDto result = service.read(1);

        Mockito.verify(authorDao).read(1);
        assertEquals(Long.valueOf(author.getId()), Long.valueOf(result.getId()));
        assertEquals(author.getName(), result.getName());

    }

    @Test
    public void updateAuthorValid() {

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setName("Test name");
        authorDto.setSurname("Test surname");

        service.update(authorDto);

        Author author = new Author(1L, "Test name", "Test surname");

        Mockito.verify(authorDao).update(author);

    }

    @Test
    public void deleteTagValid() {

        Mockito.when(authorDao.getNewsIdByAuthor(1)).thenReturn(Arrays.asList(3L, 4L));

        service.delete(1);

        Mockito.verify(newsDao).delete(3);
        Mockito.verify(newsDao).delete(4);
        Mockito.verify(authorDao).delete(1);

    }

    @Test
    public void saveAuthorAlreadyExistsValid() {
        Author defaultAuthor = new Author(500L, "check", "check");
        Mockito.when(authorDao.read(500)).thenReturn(defaultAuthor);

        AuthorDto dto = new AuthorDto(500L, "check", "check");

        service.upload(dto);

        Mockito.verify(authorDao).read(500);
    }

    @Test
    public void saveAuthorNotExistValid() {
        Author defaultAuthor = new Author(null, "check", "check");

        AuthorDto dto = new AuthorDto(null, "check", "check");

        Mockito.when(authorDao.read(-1)).thenReturn(defaultAuthor);

        service.upload(dto);

        Mockito.verify(authorDao).create(defaultAuthor);
    }

    @Test
    public void getAllValid() {
        service.getAll();
        Mockito.verify(authorDao).getAll();
    }
}
