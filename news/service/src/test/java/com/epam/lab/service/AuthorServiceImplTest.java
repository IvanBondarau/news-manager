package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@RunWith(JUnit4.class)
public class AuthorServiceImplTest {

    @Mock
    private AuthorDao authorDao;

    @Mock
    private NewsDao newsDao;

    private AuthorService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        service = new AuthorServiceImpl(authorDao, newsDao);
    }

    @Test
    public void createNewAuthorValid() {

        Mockito.when(authorDao.findByNameSurname(any(), any())).thenReturn(Optional.empty());

        AuthorDto authorDto = new AuthorDto();
        authorDto.setName("Test name");
        authorDto.setSurname("Test surname");

        Author resultEntity = new Author("Test name", "Test surname");

        service.create(authorDto);

        Mockito.verify(authorDao).findByNameSurname("Test name", "Test surname");
        Mockito.verify(authorDao).create(resultEntity);

    }

    @Test(expected = RuntimeException.class)
    public void createAuthorAlreadyExistValid() {


        Author resultEntity = new Author(1, "Test name", "Test surname");

        Mockito.when(authorDao.findByNameSurname(any(), any())).thenReturn(Optional.of(resultEntity));

        AuthorDto authorDto = new AuthorDto();
        authorDto.setName("Test name");
        authorDto.setSurname("Test surname");

        service.create(authorDto);

    }

    @Test
    public void readAuthorValid() {

        Author author = new Author(1, "Test name", "Test surname");

        Mockito.when(authorDao.read(1)).thenReturn(author);

        AuthorDto result = service.read(1);

        Mockito.verify(authorDao).read(1);
        assertEquals(Long.valueOf(author.getId()), Long.valueOf(result.getId()));
        assertEquals(author.getName(), result.getName());

    }

    @Test
    public void updateAuthorValid() {

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1);
        authorDto.setName("Test name");
        authorDto.setSurname("Test surname");

        service.update(authorDto);

        Author author = new Author(1, "Test name", "Test surname");

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
}