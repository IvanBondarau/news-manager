package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dto.AuthorDto;
import com.epam.lab.exception.ResourceAlreadyExistException;
import com.epam.lab.model.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private AuthorDao authorDao;
    private NewsDao newsDao;

    @Autowired
    public AuthorServiceImpl(AuthorDao authorDao, NewsDao newsDao) {
        this.authorDao = authorDao;
        this.newsDao = newsDao;
    }

    @Override
    @Transactional
    public void create(AuthorDto dto) {
        String name = dto.getName();
        String surname = dto.getSurname();

        Optional<Author> searchResult = authorDao.findByNameSurname(name, surname);

        if (searchResult.isPresent()) {
            throw new ResourceAlreadyExistException(dto.toString() + " already exist", searchResult.get().getId());
        } else {
            long id = authorDao.create(new Author(name, surname));
            dto.setId(id);
        }
    }

    @Override
    @Transactional
    public AuthorDto read(long id) {
        Author loaded = authorDao.read(id);
        AuthorDto result = new AuthorDto();
        result.setId(loaded.getId());
        result.setName(loaded.getName());
        result.setSurname(loaded.getSurname());
        return result;
    }

    @Override
    @Transactional
    public void update(AuthorDto dto) {
        Author author = new Author(
                dto.getId(),
                dto.getName(),
                dto.getSurname()
        );
        authorDao.update(author);
    }

    @Override
    @Transactional
    public void delete(long id) {
        List<Long> dependentNews = authorDao.getNewsIdByAuthor(id);

        for (Long newsId: dependentNews) {
            newsDao.delete(newsId);
        }

        authorDao.delete(id);

    }


}
