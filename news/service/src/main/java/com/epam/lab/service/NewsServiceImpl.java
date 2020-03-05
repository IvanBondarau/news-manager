package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.FilterCriteria;
import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.SortOrder;
import com.epam.lab.dto.TagDto;
import com.epam.lab.dto.comparator.NewsAuthorComparator;
import com.epam.lab.dto.comparator.NewsDateComparator;
import com.epam.lab.dto.comparator.NewsTagComparator;
import com.epam.lab.dto.converter.NewsConverter;
import com.epam.lab.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final EnumMap<SortOrder, Comparator<NewsDto>> SORT_ORDER_COMPARATOR_ENUM_MAP
            = new EnumMap<>(SortOrder.class);

    static {
        SORT_ORDER_COMPARATOR_ENUM_MAP.put(SortOrder.BY_DATE, new NewsDateComparator());
        SORT_ORDER_COMPARATOR_ENUM_MAP.put(SortOrder.BY_AUTHOR, new NewsAuthorComparator());
        SORT_ORDER_COMPARATOR_ENUM_MAP.put(SortOrder.BY_TAGS, new NewsTagComparator());
    }

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
        setCurrentCreationDate(newsDto);

        saveNewsAuthor(newsDto);
        saveNewsTags(newsDto);

        News entity = newsConverter.convertToEntity(newsDto);

        setNewsAuthor(entity);
        setNewsTags(entity);

        newsDao.create(entity);
        newsDto.setId(entity.getId());
    }

    private void setCurrentCreationDate(NewsDto newsDto) {
        newsDto.setCreationDate(new Date());
        newsDto.setModificationDate(newsDto.getCreationDate());
    }

    private void setNewsAuthor(News entity) {
        entity.setAuthors(entity.getAuthors()
                .stream()
                .map(author -> authorDao.read(author.getId()))
                .collect(Collectors.toSet()));
    }

    private void setNewsTags(News entity) {
        entity.setTags(entity.getTags() == null ? new HashSet<>() :
                entity.getTags()
                        .stream()
                        .map(tag -> tagDao.read(tag.getId()))
                        .collect(Collectors.toSet()));
    }


    @Override
    @Transactional
    public NewsDto read(long id) {
        News newsEntity = newsDao.read(id);

        return newsConverter.convertToDto(newsEntity);
    }

    @Override
    @Transactional
    public void update(NewsDto newsDto) {

        saveNewsAuthor(newsDto);
        saveNewsTags(newsDto);

        News entity = newsConverter.convertToEntity(newsDto);
        entity.setModificationDate(new Date());

        newsDao.update(newsConverter.convertToEntity(newsDto));

    }

    private void saveNewsAuthor(NewsDto newsDto) {
        authorService.save(newsDto.getAuthor());
    }

    private void saveNewsTags(NewsDto newsDto) {
        newsDto.getTags().forEach((TagDto tagDto) -> tagService.save(tagDto));
    }


    @Override
    public void delete(long id) {
        newsDao.delete(id);
    }

    @Override
    @Transactional
    public List<NewsDto> getAll() {
        return newsDao.getAll().stream()
                .map(newsEntity -> newsConverter.convertToDto(newsEntity))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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
                && filterCriteria.getTagNames().isEmpty());
    }

    private List<NewsDto> searchByCriteriaParams(FilterCriteria filterCriteria) {
        Set<Long> searchResult = null;

        searchResult = searchByTagNames(filterCriteria.getTagNames(), searchResult);
        searchResult = searchByAuthorId(filterCriteria.getAuthorId(), searchResult);
        searchResult = searchByAuthorName(filterCriteria.getAuthorName(), searchResult);
        searchResult = searchByAuthorSurname(filterCriteria.getAuthorSurname(), searchResult);

        if (searchResult == null || searchResult.isEmpty()) {
            return new ArrayList<>();
        } else {
            return readAll(searchResult);
        }
    }

    private Comparator<NewsDto> addNextComparator(Comparator<NewsDto> comparator, SortOrder sortOrder) {
        Comparator<NewsDto> nextComparator;

        nextComparator = SORT_ORDER_COMPARATOR_ENUM_MAP.get(sortOrder);

        if (comparator == null) {
            return nextComparator;
        } else {
            return comparator.thenComparing(nextComparator);
        }
    }

    private Set<Long> searchByAuthorId(Long authorId, Set<Long> previousResult) {
        if (authorId != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.findNewsByAuthorId(authorId));
            return joinSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> searchByAuthorName(String authorName, Set<Long> previousResult) {
        if (authorName != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.findNewsByAuthorName(authorName));
            return joinSearchResults(previousResult, searchResult);
        } else {
            return previousResult;
        }
    }

    private Set<Long> searchByAuthorSurname(String authorSurname, Set<Long> previousResult) {
        if (authorSurname != null) {
            Set<Long> searchResult = new HashSet<>(authorDao.findNewsByAuthorSurname(authorSurname));
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
    @Transactional
    public long count() {
        return newsDao.count();
    }
}
