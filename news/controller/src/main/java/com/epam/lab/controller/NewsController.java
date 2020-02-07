package com.epam.lab.controller;

import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.SortOrder;
import com.epam.lab.dto.SearchCriteria;
import com.epam.lab.exception.*;
import com.epam.lab.service.NewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
public class NewsController {
    @Autowired
    private NewsService newsService;
    @Autowired
    private RequestParamsParser parser;

    /**
     * This method reads the news by id
     * @param id  id of read news
     * @return  NewsDto with all news parameters, tags and author
     */
    @GetMapping(value = "/news/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsDto getNews(@PathVariable("id") long id) {
        return newsService.read(id);
    }

    /**
     * This method search, filter and sort news by specified criteria
     * All parameters are optional
     * @param authorId News author id (optional)
     * @param authorName News author name (optional)
     * @param authorSurname News author surname (optional)
     * @param tagNames String of required tags, separated by commas (optional)
     * @param orderParams String of params, separated by commas, that specify
     *                    sort order of the resulting news.
     *                    Acceptable params: 'byDate','byAuthor','byTags'.
     *                    Optional.
     * @return List of NewsDto, that satisfy specified search criteria, ordered by orderParams
     */
    @GetMapping(value = "/news")
    @ResponseStatus(HttpStatus.OK)
    public List<NewsDto> findNews(
            @RequestParam(name = "authorId", required = false) Long authorId,
            @RequestParam(name = "authorName", required = false) String authorName,
            @RequestParam(name = "authorSurname", required = false) String authorSurname,
            @RequestParam(name = "tagNames", required = false) String tagNames,
            @RequestParam(name = "order", required = false) String orderParams) {

        SearchCriteria searchCriteria = new SearchCriteria();

        searchCriteria.setAuthorId(authorId);
        searchCriteria.setAuthorName(authorName);
        searchCriteria.setAuthorSurname(authorSurname);

        Set<String> tagNamesSet = parser.parseTagNames(tagNames);
        searchCriteria.setTagNames(tagNamesSet);
        List<SortOrder> sortOrders = parser.parseOrderParams(orderParams);
        searchCriteria.setSortParams(sortOrders);

        return newsService.search(searchCriteria);
    }

    /**
     * This method saves received NewsDto into the database.
     * If the NewsDto contains authorId, then author would be read from the database.
     * Otherwise new author would be created.
     * If the NewsDto contains tag, which is already presented in database, then existing tag would be used.
     * Otherwise new tag would be created.
     * @param newsDto NewsDto to be created. All field should be specified except
     *                id, creation date and modification date: these fields would be set.
     * @return NewsDto that was created
     */
    @PostMapping(value = "/news")
    @ResponseStatus(HttpStatus.CREATED)
    public NewsDto createNews(@RequestBody NewsDto newsDto) {
        newsService.create(newsDto);
        return newsDto;
    }

    /**
     * Updates existing news item.
     * All fields of the received NewsDto would be set to the news, specified by id,
     * except creation date, modification date (both are set by server) and id (can't be changed).
     * During the update new author and new tags can be created
     * @param newsDto NewsDto with updated fields.
     * @param id id of the news that should be updated.
     * @return Updated NewsDto
     */
    @PutMapping(value = "/news/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsDto updateNews(@RequestBody NewsDto newsDto, @PathVariable long id) {
        newsDto.setId(id);
        newsService.update(newsDto);
        return newsDto;
    }

    /**
     * Deletes the news by specified Id
     * @param id Id of the news to be deleted
     */
    @DeleteMapping(value = "/news/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteNews(@PathVariable long id) {
        newsService.delete(id);
    }





}
