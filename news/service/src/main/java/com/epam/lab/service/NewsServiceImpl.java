package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.*;
import com.epam.lab.exception.NewsNotFoundException;
import com.epam.lab.exception.ResourceNotFoundException;
import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
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

    private AuthorService authorService;

    @Autowired
    public NewsServiceImpl(NewsDao newsDao, AuthorDao authorDao, TagDao tagDao, AuthorService authorService) {
        this.newsDao = newsDao;
        this.authorDao = authorDao;
        this.tagDao = tagDao;
        this.authorService = authorService;
    }

    @Override
    public void create(NewsDto dto) {


        if (dto.getAuthor().getId() == 0) {
            authorService.create(dto.getAuthor());
        } else {
            dto.setAuthor(authorService.read(dto.getAuthor().getId()));
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

        if (dto.getAuthor().getId() == 0) {
            authorService.create(dto.getAuthor());
        } else {
            dto.setAuthor(authorService.read(dto.getAuthor().getId()));
        }


        dto.setModificationDate(Date.valueOf(LocalDate.now()));
        NewsDto oldDto;
        try {
             oldDto = read(dto.getId());
        }  catch (ResourceNotFoundException e) {
            throw new NewsNotFoundException(e.getResourceId());
        }
        if (!oldDto.getAuthor().equals(dto.getAuthor())) {
            newsDao.deleteNewsAuthor(dto.getId());
            newsDao.setNewsAuthor(dto.getId(), dto.getAuthor().getId());
        }

        updateTags(oldDto, dto);

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

    @Transactional
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

        if (searchCriteria.getTagNames() != null) {
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

    private static final class NewsDateComparator implements Comparator<NewsDto> {
        @Override
        public int compare(NewsDto o1, NewsDto o2) {
            if (o1.getCreationDate() == o2.getCreationDate()) {
                return 0;
            }
            if (o1.getCreationDate() == null) {
                return -1;
            }
            if (o2.getCreationDate() == null) {
                return 1;
            }
            return o1.getCreationDate().compareTo(o2.getCreationDate());
        }
    }

    private static final class NewsAuthorComparator implements Comparator<NewsDto> {

        @Override
        public int compare(NewsDto o1, NewsDto o2) {
            if (o1.getAuthor() == o2.getAuthor()) {
                return 0;
            }
            if (o1.getAuthor() == null) {
                return -1;
            }
            if (o2.getAuthor() == null) {
                return 1;
            }
            return (o1.getAuthor().toString().compareTo(o2.getAuthor().toString()));

        }
    }

    private static final class NewsTagComparator implements Comparator<NewsDto> {

        @Override
        public int compare(NewsDto o1, NewsDto o2) {
            if (o1.getTags() == o2.getTags()) {
                return 0;
            }
            if (o1.getTags() == null) {
                return -1;
            }
            if (o2.getTags() == null) {
                return 1;
            }
            String tags1 = o1.getTags().stream().map((TagDto::getName)).sorted().collect(Collectors.joining(","));
            String tags2 = o2.getTags().stream().map((TagDto::getName)).sorted().collect(Collectors.joining(","));

            return tags1.compareTo(tags2);

        }
    }
}
