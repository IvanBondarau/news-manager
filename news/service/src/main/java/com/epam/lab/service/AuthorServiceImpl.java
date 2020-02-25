package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dto.converter.AuthorConverter;
import com.epam.lab.dto.AuthorDto;
import com.epam.lab.model.Author;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private static final Logger LOGGER = Logger.getLogger(AuthorServiceImpl.class);

    private AuthorDao authorDao;
    private NewsDao newsDao;

    private AuthorConverter authorConverter;

    @Autowired
    public AuthorServiceImpl(AuthorDao authorDao, NewsDao newsDao, AuthorConverter authorConverter) {
        this.authorDao = authorDao;
        this.newsDao = newsDao;
        this.authorConverter = authorConverter;
    }

    @Override
    @Transactional
    public void create(AuthorDto dto) {
        LOGGER.info(dto);
        Author entity = authorConverter.convertToEntity(dto);
        authorDao.create(entity);
        dto.setId(entity.getId());
    }

    @Override
    @Transactional
    public AuthorDto read(long id) {
        Author author = authorDao.read(id);
        return authorConverter.convertToDto(author);
    }

    @Override
    @Transactional
    public void update(AuthorDto dto) {
        Author author = authorConverter.convertToEntity(dto);
        authorDao.update(author);
    }

    @Override
    @Transactional
    public void delete(long id) {
        List<Long> dependentNews = authorDao.findNewsByAuthorId(id);

        for (Long newsId : dependentNews) {
            newsDao.delete(newsId);
        }

        authorDao.delete(id);
    }


    @Override
    @Transactional
    public void save(AuthorDto authorDto) {
        if (isAuthorIdDefined(authorDto)) {
            loadAuthor(authorDto);
        } else {
            create(authorDto);
        }
    }


    private boolean isAuthorIdDefined(@NotNull AuthorDto authorDto) {
        return authorDto.getId() != null;
    }

    @Override
    @Transactional
    public List<AuthorDto> getAll() {
        return authorDao.getAll().stream()
                .map(author -> authorConverter.convertToDto(author))
                .collect(Collectors.toList());
    }

    private void loadAuthor(AuthorDto authorDto) {
        long authorId = authorDto.getId();
        AuthorDto loadedDto = this.read(authorId);
        authorDto.setName(loadedDto.getName());
        authorDto.setSurname(loadedDto.getSurname());
    }


}
