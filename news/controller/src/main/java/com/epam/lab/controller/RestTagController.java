package com.epam.lab.controller;

import com.epam.lab.dto.TagDto;
import com.epam.lab.service.TagService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestTagController {

    private static final Logger logger = Logger.getLogger(RestTagController.class);

    @Autowired
    private TagService tagService;

    /**
     * Reads existing tag by id
     * @param id Tag id
     * @return TagDto that corresponds to the specified id
     */
    @GetMapping(value = "/tag/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDto getTag(@PathVariable("id") long id) {
        logger.info("New get tag request");
        logger.info("Tag id = " + id);
        return tagService.read(id);
    }

    /**
     * Creates new tag. If tag with such name already exists, exception would be thrown
     * @param tagDto TagDto to be saved. Should contain unique tag name
     * @return TagDto that was created
     */
    @PostMapping(value = "/tag")
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto createTag(@RequestBody TagDto tagDto) {
        logger.info("New create tag request");
        logger.info("Tag = " + tagDto);
        tagService.create(tagDto);
        return tagDto;
    }

    /**
     * Updates tag by specified id
     * @param tagDto TagDto, which should contain new tag name.
     * @param id id of tag that should be updated
     * @return Updated TagDto
     */
    @PutMapping(value = "/tag/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDto updateTag(@RequestBody TagDto tagDto, @PathVariable long id) {
        logger.info("New update tag request");
        logger.info("Tag id = " + id);
        logger.info("Updated tag = " + tagDto);
        tagDto.setId(id);
        tagService.update(tagDto);
        return tagDto;
    }

    /**
     * Delete tag by id
     * @param id id of the tag to be deleted
     */
    @DeleteMapping(value = "/tag/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTag(@PathVariable long id) {
        logger.info("New delete tag request");
        logger.info("Tag id = " + id);
        tagService.delete(id);
    }
}
