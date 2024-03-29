package com.epam.lab.controller;

import com.epam.lab.dto.AuthorDto;
import com.epam.lab.exception.InvalidRequestFormatException;
import com.epam.lab.service.AuthorService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/author")
public class AuthorRestController {

    private static final Logger LOGGER = Logger.getLogger(AuthorRestController.class);

    private static final String AUTHOR_ID_EQUAL = "Author id = ";

    @Autowired
    private AuthorService authorService;

    /**
     * Reads existing author by id
     *
     * @param id Author id
     * @return AuthorDto that corresponds to the specified id
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDto getAuthor(@PathVariable("id") long id) {
        LOGGER.info("New get author request");
        LOGGER.info(AUTHOR_ID_EQUAL + id);
        return authorService.read(id);
    }

    /**
     * Returns list of all saved in database authors
     *
     * @return list of all saved authors
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorDto> getAll() {
        return authorService.getAll();
    }

    /**
     * Creates new author
     *
     * @param authorDto Author to be saved.
     * @return AuthorDto with specified id
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody @Valid AuthorDto authorDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.error("Create author: unsuccessful binding");
            throw new InvalidRequestFormatException(bindingResult.toString());
        }
        LOGGER.info("New create author request");
        LOGGER.info(AUTHOR_ID_EQUAL + authorDto);
        authorService.create(authorDto);

        URI resourceLocation = getResourceLocation(authorDto.getId());
        return ResponseEntity.created(resourceLocation).body(authorDto);
    }

    /**
     * Updates author by specified id
     *
     * @param authorDto AuthorDto, which contains new author name and/or surname
     * @param id        id of the author that should be updated
     * @return Updated AuthorDto
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDto updateAuthor(@RequestBody @Valid AuthorDto authorDto,
                                  @PathVariable long id,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.error("Update author: unsuccessful binding");
            throw new InvalidRequestFormatException(bindingResult.toString());
        }

        LOGGER.info("New update author request");
        LOGGER.info(AUTHOR_ID_EQUAL + id);
        LOGGER.info("Updated author = " + authorDto);
        authorDto.setId(id);
        authorService.update(authorDto);
        return authorDto;
    }


    /**
     * Delete author by id
     *
     * @param id id of the author to be deleted
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAuthor(@PathVariable long id) {
        LOGGER.info("New delete author request");
        LOGGER.info(AUTHOR_ID_EQUAL + id);
        authorService.delete(id);
    }


    private URI getResourceLocation(long resourceId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resourceId).toUri();
    }
}
