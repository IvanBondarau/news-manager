package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.*;
import com.epam.lab.exception.AuthorNotFoundException;
import com.epam.lab.exception.NewsNotFoundException;
import com.epam.lab.exception.ResourceNotFoundException;
import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private NewsDao newsDao;
    private AuthorDao authorDao;
    private TagDao tagDao;


    @Autowired
    public NewsServiceImpl(NewsDao newsDao, AuthorDao authorDao, TagDao tagDao) {
        this.newsDao = newsDao;
        this.authorDao = authorDao;
        this.tagDao = tagDao;
    }

    @Override
    @Transactional
    public void create(NewsDto dto) {

        if (dto.getAuthor().getId() == 0) {
            Author author = new Author(dto.getAuthor().getName(), dto.getAuthor().getSurname());
            long id = authorDao.create(author);
            dto.getAuthor().setId(id);
        } else {
            try {
                Author author = authorDao.read(dto.getAuthor().getId());
                dto.getAuthor().setName(author.getName());
                dto.getAuthor().setSurname(author.getSurname());
            } catch (ResourceNotFoundException e) {
                throw new AuthorNotFoundException(e.getResourceId());
            }
        }

        dto.setCreationDate(Date.valueOf(LocalDate.now()));
        dto.setModificationDate(dto.getCreationDate());

        News entity = buildNewsFromDto(dto);

        long id = newsDao.create(entity);
        dto.setId(id);

        newsDao.setNewsAuthor(dto.getId(), dto.getAuthor().getId());

        for (TagDto tagDto : dto.getTags()) {
            createOrGet(tagDto);
            newsDao.setNewsTag(dto.getId(), tagDto.getId());
        }
    }

    @Override
    @Transactional
    public NewsDto read(long id) {
        News entity;
        try {
            entity = newsDao.read(id);
        } catch (ResourceNotFoundException e) {
            throw new NewsNotFoundException(e.getResourceId());
        }

        NewsDto dto = new NewsDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setShortText(entity.getShortText());
        dto.setFullText(entity.getFullText());
        dto.setCreationDate(entity.getCreationDate());
        dto.setModificationDate(entity.getModificationDate());

        long authorId = newsDao.getAuthorIdByNews(dto.getId());
        Author author = authorDao.read(authorId);
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(authorId);
        authorDto.setName(author.getName());
        authorDto.setSurname(author.getSurname());

        dto.setAuthor(authorDto);

        List<Long> tagIds = newsDao.getTagsIdForNews(dto.getId());

        dto.setTags(tagIds.stream().map((Long tagId) -> {
            Tag tag = tagDao.read(tagId);
            TagDto tagDto = new TagDto();
            tagDto.setId(tag.getId());
            tagDto.setName(tag.getName());
            return tagDto;
        }).collect(Collectors.toSet()));

        return dto;
    }

    @Override
    public void update(NewsDto dto) {

        /*
         *  Read or create news author
         *  If author id is received, then try to read author from the database
         *  Otherwise create new author with specified name and surname
         */
        if (dto.getAuthor().getId() == 0) {
            Author author = new Author(dto.getAuthor().getName(), dto.getAuthor().getSurname());
            long id = authorDao.create(author);
            dto.getAuthor().setId(id);
        } else {
            try {
                Author author = authorDao.read(dto.getAuthor().getId());
                dto.getAuthor().setName(author.getName());
                dto.getAuthor().setSurname(author.getSurname());
            } catch (ResourceNotFoundException e) {
                throw new AuthorNotFoundException(e.getResourceId());
            }
        }

        //Read old news version
        NewsDto oldDto;
        try {
             oldDto = read(dto.getId());
        }  catch (ResourceNotFoundException e) {
            throw new NewsNotFoundException(e.getResourceId());
        }

        //Update author
        if (!oldDto.getAuthor().equals(dto.getAuthor())) {
            newsDao.deleteNewsAuthor(dto.getId());
            newsDao.setNewsAuthor(dto.getId(), dto.getAuthor().getId());
        }

        //Update tags
        updateTags(oldDto, dto);

        //Update fields
        dto.setCreationDate(oldDto.getCreationDate());
        dto.setModificationDate(Date.valueOf(LocalDate.now()));
        News entity = buildNewsFromDto(dto);
        entity.setId(dto.getId());
        newsDao.update(entity);
    }

    @Override
    public void delete(long id) {
        try {
            newsDao.delete(id);
        } catch (ResourceNotFoundException e) {
            throw new NewsNotFoundException(e.getResourceId());
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void createOrGet(TagDto dto) {
        Optional<Tag> searchResult = tagDao.findByName(dto.getName());
        if (searchResult.isPresent()) {
            dto.setId(searchResult.get().getId());
        } else {
            long id = tagDao.create(new Tag(dto.getName()));
            dto.setId(id);
        }
    }

    private News buildNewsFromDto(NewsDto dto) {
        return new News(
                dto.getTitle(),
                dto.getShortText(),
                dto.getFullText(),
                dto.getCreationDate(),
                dto.getModificationDate()
        );
    }

    @Transactional
    private void updateTags(NewsDto oldDto, NewsDto dto) {
        for (TagDto tagDto : dto.getTags()) {
            createOrGet(tagDto);
        }

        Set<TagDto> tagsToRemove = new HashSet<>(oldDto.getTags());
        tagsToRemove.removeAll(dto.getTags());

        for (TagDto tagDto : tagsToRemove) {
            newsDao.deleteNewsTag(oldDto.getId(), tagDto.getId());
        }

        Set<TagDto> tagsToAdd = new HashSet<>(dto.getTags());
        tagsToAdd.removeAll(oldDto.getTags());

        for (TagDto tagDto : tagsToAdd) {
            newsDao.setNewsTag(oldDto.getId(), tagDto.getId());
        }
    }

    @Override
    public List<NewsDto> search(SearchCriteria searchCriteria) {
        Set<Long> idResultSet = null;

        if (searchCriteria.getTagNames() != null && (!searchCriteria.getTagNames().isEmpty())) {
            idResultSet = new HashSet<>(tagDao.findNewsIdByTagNames(searchCriteria.getTagNames()));
        }

        if (searchCriteria.getAuthorId() != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.getNewsIdByAuthor(searchCriteria.getAuthorId()));
            if (idResultSet == null) {
                idResultSet = searchResult;
            } else {
                idResultSet.retainAll(searchResult);
            }
        }

        if (searchCriteria.getAuthorName() != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.getNewsIdByAuthorName(searchCriteria.getAuthorName()));
            if (idResultSet == null) {
                idResultSet = searchResult;
            } else {
                idResultSet.retainAll(searchResult);
            }
        }

        if (searchCriteria.getAuthorSurname() != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.getNewsIdByAuthorName(searchCriteria.getAuthorSurname()));
            if (idResultSet == null) {
                idResultSet = searchResult;
            } else {
                idResultSet.retainAll(searchResult);
            }
        }

        if (idResultSet == null) {
            return new ArrayList<>();
        }

        List<NewsDto> resultSet = new ArrayList<>();
        for (Long newsId: idResultSet) {
            resultSet.add(this.read(newsId));
        }

        if (searchCriteria.getSortParams() != null) {
            Comparator<NewsDto> comparator = null;
            for (SortOrder sortOrder: searchCriteria.getSortParams()) {
                if (sortOrder == SortOrder.BY_DATE) {
                    comparator = comparator == null ?
                            new NewsDateComparator()
                            : comparator.thenComparing(new NewsDateComparator());
                }

                if (sortOrder == SortOrder.BY_AUTHOR) {
                    comparator = comparator == null ?
                            new NewsAuthorComparator()
                            : comparator.thenComparing(new NewsAuthorComparator());
                }

                if (sortOrder == SortOrder.BY_TAGS) {
                    comparator = comparator == null ?
                            new NewsTagComparator()
                            : comparator.thenComparing(new NewsTagComparator());
                }

            }

            resultSet.sort(comparator);

        }

        return resultSet;
    }
}
