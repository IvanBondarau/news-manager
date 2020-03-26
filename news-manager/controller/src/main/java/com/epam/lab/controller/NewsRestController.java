package com.epam.lab.controller;

import com.epam.lab.dto.FilterCriteria;
import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.SortOrder;
import com.epam.lab.exception.InvalidRequestFormatException;
import com.epam.lab.service.NewsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping(value = "/news")
public class NewsRestController {

    private static final Logger LOGGER = Logger.getLogger(NewsRestController.class);

    private static final String NEWS_ID_EQUAL = "News id = ";

    @Autowired
    private NewsService newsService;
    @Autowired
    private RequestParamsParser parser;

    /**
     * This method reads the news by id
     *
     * @param id id of read news
     * @return NewsDto with fields, tags and author
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsDto getNews(@PathVariable("id") long id) {
        LOGGER.info("New get news request");
        LOGGER.info(NEWS_ID_EQUAL + id);
        return newsService.read(id);
    }

    /**
     * This method search, filter and sort news by specified criteria
     * All parameters are optional
     *
     * @param authorId      News author id (optional)
     * @param authorName    News author name (optional)
     * @param authorSurname News author surname (optional)
     * @param tagNames      String of required tags, separated by commas (optional)
     * @param orderParams   String of params, separated by commas, that specify
     *                      sort order of the resulting news.
     *                      Acceptable params: 'byDate','byAuthor','byTags'.
     *                      Optional.
     * @return List of NewsDto, that satisfy specified search criteria, ordered by orderParams
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NewsDto> findNews(
            @RequestParam(name = "authorId", required = false) Long authorId,
            @RequestParam(name = "authorName", required = false) String authorName,
            @RequestParam(name = "authorSurname", required = false) String authorSurname,
            @RequestParam(name = "tagNames", required = false) String tagNames,
            @RequestParam(name = "order", required = false) String orderParams) {

        FilterCriteria filterCriteria = new FilterCriteria();

        filterCriteria.setAuthorId(authorId);
        filterCriteria.setAuthorName(authorName);
        filterCriteria.setAuthorSurname(authorSurname);

        Set<String> tagNamesSet = parser.parseTagNames(tagNames);
        filterCriteria.setTagNames(tagNamesSet);
        List<SortOrder> sortOrders = parser.parseOrderParams(orderParams);
        filterCriteria.setSortParams(sortOrders);

        return newsService.filter(filterCriteria);
    }

    /**
     * This method saves received NewsDto into the database.
     * If the NewsDto contains authorId, then author would be read from the database.
     * Otherwise new author would be created.
     * If the NewsDto contains tag, which is already presented in database, then existing tag would be used.
     * Otherwise new tag would be created.
     *
     * @param newsDto NewsDto to be created. All field should be specified except
     *                id, creation date and modification date: these fields would be set.
     * @return NewsDto that was created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NewsDto> createNews(@RequestBody @NotNull @Valid NewsDto newsDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.error("Create news: unsuccessful binding");
            throw new InvalidRequestFormatException(bindingResult.toString());
        }

        LOGGER.info("New create news request");
        LOGGER.info(NEWS_ID_EQUAL + newsDto);
        newsService.create(newsDto);

        URI recourseLocation = getResourceLocation(newsDto.getId());
        return ResponseEntity.created(recourseLocation).body(newsDto);
    }

    /**
     * Updates existing news item.
     * All fields of the received NewsDto would be set to the news, specified by id,
     * except creation date, modification date (both are set by server) and id (can't be changed).
     * During the update new author and new tags can be created
     *
     * @param newsDto NewsDto with updated fields.
     * @param id      id of the news that should be updated.
     * @return Updated NewsDto
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsDto updateNews(@RequestBody @NotNull @Valid NewsDto newsDto, @PathVariable long id,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.error("Update news: unsuccessful binding");
            throw new InvalidRequestFormatException(bindingResult.toString());
        }

        LOGGER.info("New update news request");
        LOGGER.info(NEWS_ID_EQUAL + id);
        LOGGER.info("Updated news = " + newsDto);
        newsDto.setId(id);
        newsService.update(newsDto);
        return newsDto;
    }

    /**
     * Deletes the news by specified Id
     *
     * @param id Id of the news to be deleted
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteNews(@PathVariable long id) {
        LOGGER.info("New delete news request");
        LOGGER.info(NEWS_ID_EQUAL + id);
        newsService.delete(id);
    }


    /**
     * Counts news in the database
     *
     * @return number of news, saved in the database
     */
    @GetMapping(value = "/count")
    @ResponseStatus(HttpStatus.OK)
    public long countNews() {
        return newsService.count();
    }

    private URI getResourceLocation(long resourceId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resourceId).toUri();
    }


}
