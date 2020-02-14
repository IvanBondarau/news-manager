package com.epam.lab.service;

import com.epam.lab.converter.NewsConverter;
import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.FilterCriteria;
import com.epam.lab.dto.SortOrder;
import com.epam.lab.dto.TagDto;
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

        uploadNewsAuthor(newsDto);
        uploadNewsTags(newsDto);

        setCurrentCreationDate(newsDto);
        News entity = newsConverter.convertToEntity(newsDto);
        long id = newsDao.create(entity);
        newsDto.setId(id);

        newsDao.setNewsAuthor(newsDto.getId(), newsDto.getAuthor().getId());
        newsDto.getTags().forEach(
                (TagDto tagDto) -> newsDao.setNewsTag(newsDto.getId(), tagDto.getId())
        );
    }

    private void setCurrentCreationDate(NewsDto newsDto) {
        newsDto.setCreationDate(Date.valueOf(LocalDate.now()));
        newsDto.setModificationDate(newsDto.getCreationDate());
    }

    @Override
    @Transactional
    public NewsDto read(long id) {
        News newsEntity = newsDao.read(id);
        NewsDto newsDto = newsConverter.convertToDto(newsEntity);

        readNewsAuthor(newsDto);
        readNewsTags(newsDto);

        return newsDto;
    }

    private void readNewsAuthor(NewsDto newsDto) {
        long authorId = newsDao.getAuthorIdByNews(newsDto.getId());
        newsDto.setAuthor(authorService.read(authorId));
    }

    private void readNewsTags(NewsDto newsDto) {
        List<Long> tagIds = newsDao.getTagsIdForNews(newsDto.getId());

        newsDto.setTags(
                tagIds.stream()
                        .map((Long tagId) -> tagService.read(tagId))
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public void update(NewsDto newsDto) {

        uploadNewsAuthor(newsDto);
        uploadNewsTags(newsDto);

        NewsDto oldNewsDto = read(newsDto.getId());

        updateNewsAuthor(oldNewsDto, newsDto);
        updateNewsTags(oldNewsDto, newsDto);
        updateNewsFields(oldNewsDto, newsDto);

    }

    private void uploadNewsAuthor(NewsDto newsDto) {
        authorService.upload(newsDto.getAuthor());
    }

    private void uploadNewsTags(NewsDto newsDto) {
        newsDto.getTags().forEach((TagDto tagDto) -> tagService.upload(tagDto));
    }


    private void updateNewsTags(NewsDto oldNewsDto, NewsDto receivedNewsDto) {
        removeOutdatedTags(oldNewsDto, receivedNewsDto);
        saveUpdatedTags(oldNewsDto, receivedNewsDto);
    }

    private void removeOutdatedTags(NewsDto oldNewsDto, NewsDto receivedNewsDto) {
        Set<TagDto> tagsToRemove = new HashSet<>(oldNewsDto.getTags());
        tagsToRemove.removeAll(receivedNewsDto.getTags());

        for (TagDto tagDto : tagsToRemove) {
            newsDao.deleteNewsTag(oldNewsDto.getId(), tagDto.getId());
        }
    }

    private void saveUpdatedTags(NewsDto oldNewsDto, NewsDto receivedNewsDto) {
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


    private void updateNewsAuthor(NewsDto oldNewsDto, NewsDto receivedNewsDto) {

        long oldAuthorId = oldNewsDto.getAuthor().getId();
        long newAuthorId = receivedNewsDto.getAuthor().getId();

        if (oldAuthorId != newAuthorId) {
            newsDao.deleteNewsAuthor(receivedNewsDto.getId());
            newsDao.setNewsAuthor(receivedNewsDto.getId(), newAuthorId);
        }
    }


    @Override
    public void delete(long id) {
        newsDao.delete(id);
    }

    @Override
    public List<NewsDto> getAll() {
        return newsDao.getAll().stream()
                .map(newsEntity -> {
                    NewsDto newsDto = newsConverter.convertToDto(newsEntity);
                    readNewsTags(newsDto);
                    readNewsAuthor(newsDto);
                    return newsDto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<NewsDto> filter(FilterCriteria filterCriteria) {

        List<NewsDto> searchResults;

        if (containsSearchParams(filterCriteria)) {
            searchResults = searchByCriteriaParams(filterCriteria);
        } else {
            searchResults = getAll();
        }

        sortBySortParams(searchResults, filterCriteria.getSortParams());

        return searchResults;
    }

    private boolean containsSearchParams(FilterCriteria filterCriteria) {
        return !(filterCriteria.getAuthorId() == null
                && filterCriteria.getAuthorName() == null
                && filterCriteria.getAuthorSurname() == null
                && filterCriteria.getTagNames() == null);
    }

    private List<NewsDto> searchByCriteriaParams(FilterCriteria filterCriteria) {
        Set<Long> searchResult = null;

        searchResult = searchByTagNames(filterCriteria.getTagNames(), searchResult);
        searchResult = searchByAuthorId(filterCriteria.getAuthorId(), searchResult);
        searchResult = searchByAuthorName(filterCriteria.getAuthorName(), searchResult);
        searchResult = searchByAuthorSurname(filterCriteria.getAuthorSurname(), searchResult);

        if (searchResult == null) {
            return new ArrayList<>();
        } else {
            return readAll(searchResult);
        }
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
            return joinSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> searchByAuthorName(String authorName, Set<Long> previousResult) {
        if (authorName != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.getNewsIdByAuthorName(authorName));
            return joinSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> searchByAuthorSurname(String authorSurname, Set<Long> previousResult) {
        if (authorSurname != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.getNewsIdByAuthorSurname(authorSurname));
            return joinSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> searchByTagNames(Set<String> tagNames, Set<Long> previousResult) {
        if (tagNames != null && (!tagNames.isEmpty())) {
            Set<Long> searchResult = new HashSet<>(tagDao.findNewsIdByTagNames(tagNames));
            return joinSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> joinSearchResults(Set<Long> firstResult, Set<Long> secondResult) {
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

    @Override
    public long count() {
        return newsDao.count();
    }
}
