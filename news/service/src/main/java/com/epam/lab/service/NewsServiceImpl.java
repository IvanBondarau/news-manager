package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.*;
import com.epam.lab.exception.NewsNotFoundException;
import com.epam.lab.exception.ResourceNotFoundException;
import com.epam.lab.model.News;
import org.springframework.beans.factory.annotation.Autowired;
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

    private TagService tagService;

    private AuthorService authorService;

    private NewsConverter newsConverter;

    @Autowired
    public NewsServiceImpl(NewsDao newsDao, AuthorDao authorDao, TagDao tagDao,
                           TagService tagService, AuthorService authorService,
                           NewsConverter newsConverter) {
        this.newsDao = newsDao;
        this.authorDao = authorDao;
        this.tagDao = tagDao;
        this.tagService = tagService;
        this.authorService = authorService;
        this.newsConverter = newsConverter;
    }

    @Override
    @Transactional
    public void create(NewsDto newsDto) {

        loadNewsAuthor(newsDto);
        loadNewsTags(newsDto);

        setCurrentCreationDate(newsDto);
        News entity = newsConverter.convertToEntity(newsDto);
        long id = newsDao.create(entity);
        newsDto.setId(id);

        newsDao.setNewsAuthor(newsDto.getId(), newsDto.getAuthor().getId());
        newsDto.getTags().forEach(
                (TagDto tagDto) -> newsDao.setNewsTag(newsDto.getId(), tagDto.getId())
        );
    }

    @Override
    @Transactional
    public NewsDto read(long id) {
        News newsEntity = newsDao.read(id);
        NewsDto newsDto = newsConverter.convertToDto(newsEntity);

        long authorId = newsDao.getAuthorIdByNews(newsDto.getId());
        newsDto.setAuthor(authorService.read(authorId));

        List<Long> tagIds = newsDao.getTagsIdForNews(newsDto.getId());

        newsDto.setTags(
                tagIds.stream()
                        .map((Long tagId) -> tagService.read(tagId))
                        .collect(Collectors.toSet())
        );

        return newsDto;
    }

    @Override
    public void update(NewsDto newsDto) {

        loadNewsAuthor(newsDto);
        loadNewsTags(newsDto);

        NewsDto oldNewsDto = read(newsDto.getId());

        updateNewsAuthor(oldNewsDto, newsDto);
        updateNewsTags(oldNewsDto, newsDto);
        updateNewsFields(oldNewsDto, newsDto);

    }

    @Override
    public void delete(long id) {
        try {
            newsDao.delete(id);
        } catch (ResourceNotFoundException e) {
            throw new NewsNotFoundException(e.getResourceId());
        }

    }


    @Override
    public List<NewsDto> search(SearchCriteria searchCriteria) {
        Set<Long> searchResult = null;

        searchResult = searchByTagNames(searchCriteria.getTagNames(), searchResult);
        searchResult = searchByAuthorId(searchCriteria.getAuthorId(), searchResult);
        searchResult = searchByAuthorName(searchCriteria.getAuthorName(), searchResult);
        searchResult = searchByAuthorSurname(searchCriteria.getAuthorSurname(), searchResult);

        if (searchResult == null) {
            return new ArrayList<>();
        }

        List<NewsDto> results = readAll(searchResult);

        sortBySortParams(results, searchCriteria.getSortParams());

        return results;
    }

    private void updateNewsTags(NewsDto oldNewsDto, NewsDto receivedNewsDto) {
        removeExtraTags(oldNewsDto, receivedNewsDto);
        addNewTags(oldNewsDto, receivedNewsDto);
    }

    private void removeExtraTags(NewsDto oldNewsDto, NewsDto receivedNewsDto) {
        Set<TagDto> tagsToRemove = new HashSet<>(oldNewsDto.getTags());
        tagsToRemove.removeAll(receivedNewsDto.getTags());

        for (TagDto tagDto : tagsToRemove) {
            newsDao.deleteNewsTag(oldNewsDto.getId(), tagDto.getId());
        }
    }

    private void addNewTags(NewsDto oldNewsDto, NewsDto receivedNewsDto) {
        Set<TagDto> tagsToAdd = new HashSet<>(receivedNewsDto.getTags());
        tagsToAdd.removeAll(oldNewsDto.getTags());

        for (TagDto tagDto : tagsToAdd) {
            newsDao.setNewsTag(oldNewsDto.getId(), tagDto.getId());
        }
    }

    private void updateNewsFields(NewsDto oldNewsDto, NewsDto newsDto) {
        newsDto.setCreationDate(oldNewsDto.getCreationDate());
        newsDto.setModificationDate(Date.valueOf(LocalDate.now()));
        News entity = newsConverter.convertToEntity(newsDto);
        newsDao.update(entity);
    }


    private void loadNewsAuthor(NewsDto newsDto) {
        authorService.loadOrCreateAuthor(newsDto.getAuthor());
    }

    private void loadNewsTags(NewsDto newsDto) {
        newsDto.getTags().forEach((TagDto tagDto) -> tagService.loadOrCreateTag(tagDto));
    }

    private void updateNewsAuthor(NewsDto oldNewsDto, NewsDto receivedNewsDto) {

        long oldAuthorId = oldNewsDto.getAuthor().getId();
        long newAuthorId = receivedNewsDto.getAuthor().getId();

        if (oldAuthorId != newAuthorId) {
            newsDao.deleteNewsAuthor(receivedNewsDto.getId());
            newsDao.setNewsAuthor(receivedNewsDto.getId(), newAuthorId);
        }
    }

    private void setCurrentCreationDate(NewsDto newsDto) {
        newsDto.setCreationDate(Date.valueOf(LocalDate.now()));
        newsDto.setModificationDate(newsDto.getCreationDate());
    }

    private Comparator<NewsDto> addNextComparator(Comparator<NewsDto> comparator, SortOrder sortOrder) {
        Comparator<NewsDto> nextComparator;

        switch (sortOrder) {
            case BY_DATE:
                nextComparator = new NewsDateComparator();
                break;
            case BY_TAGS:
                nextComparator = new NewsTagComparator();
                break;
            case BY_AUTHOR:
                nextComparator = new NewsAuthorComparator();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortOrder);
        }

        if (comparator == null) {
            return nextComparator;
        } else {
            return comparator.thenComparing(nextComparator);
        }
    }

    private Set<Long> searchByAuthorId(Long authorId, Set<Long> previousResult) {
        if (authorId != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.getNewsIdByAuthor(authorId));
            return uniteSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> searchByAuthorName(String authorName, Set<Long> previousResult) {
        if (authorName != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.getNewsIdByAuthorName(authorName));
            return uniteSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> searchByAuthorSurname(String authorSurname, Set<Long> previousResult) {
        if (authorSurname != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.getNewsIdByAuthorSurname(authorSurname));
            return uniteSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> searchByTagNames(Set<String> tagNames, Set<Long> previousResult) {
        if (tagNames != null && (!tagNames.isEmpty())) {
            Set<Long> searchResult = new HashSet<>(tagDao.findNewsIdByTagNames(tagNames));
            return uniteSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> uniteSearchResults(Set<Long> firstResult, Set<Long> secondResult) {
        if (firstResult == null) {
            return secondResult;
        } else {
            firstResult.retainAll(secondResult);
            return firstResult;
        }
    }

    private List<NewsDto> readAll(Set<Long> idList) {
        List<NewsDto> resultSet = new ArrayList<>();
        for (Long newsId : idList) {
            resultSet.add(this.read(newsId));
        }
        return resultSet;
    }

    private void sortBySortParams(List<NewsDto> news, List<SortOrder> sortParams) {
        if (sortParams != null) {
            Comparator<NewsDto> comparator = null;
            for (SortOrder sortOrder : sortParams) {
                comparator = addNextComparator(comparator, sortOrder);
            }
            news.sort(comparator);
        }
    }

}
