package com.epam.lab.controller;

import com.epam.lab.dto.AuthorDto;
import com.epam.lab.service.AuthorService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestAuthorController {

    private static final Logger logger = Logger.getLogger(RestAuthorController.class);

    @Autowired
    private AuthorService authorService;

    /**
     * Reads existing author by id
     *
     * @param id Author id
     * @return AuthorDto that corresponds to the specified id
     */
    @GetMapping(value = "/author/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDto getAuthor(@PathVariable("id") long id) {
        logger.info("New get author request");
        logger.info("Author id = " + id);
        return authorService.read(id);
    }

    /**
     * Creates new author
     *
     * @param authorDto Author to be saved.
     * @return AuthorDto with specified id
     */
    @PostMapping(value = "/author")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDto createAuthor(@RequestBody AuthorDto authorDto) {
        logger.info("New create author request");
        logger.info("Author = " + authorDto);
        authorService.create(authorDto);
        return authorDto;
    }

    /**
     * Updates author by specified id
     *
     * @param authorDto AuthorDto, which contains new author name and/or surname
     * @param id        id of the author that should be updated
     * @return Updated AuthorDto
     */
    @PutMapping(value = "/author/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDto updateAuthor(@RequestBody AuthorDto authorDto, @PathVariable long id) {
        logger.info("New update author request");
        logger.info("Author id = " + id);
        logger.info("Updated author = " + authorDto);
        authorDto.setId(id);
        authorService.update(authorDto);
        return authorDto;
    }


    /**
     * Delete author by id
     *
     * @param id id of the author to be deleted
     */
    @DeleteMapping(value = "/author/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAuthor(@PathVariable long id) {
        logger.info("New delete author request");
        logger.info("Author id = " + id);
        authorService.delete(id);
    }
}
