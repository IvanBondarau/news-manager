package com.epam.lab.controller;

import com.epam.lab.dto.TagDto;
import com.epam.lab.exception.InvalidRequestFormatException;
import com.epam.lab.service.TagService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/tag")
public class TagRestController {

    private static final Logger LOGGER = Logger.getLogger(TagRestController.class);

    private static final String TAG_ID_EQUAL = "Tag id = ";

    @Autowired
    private TagService tagService;

    /**
     * Reads existing tag by id
     *
     * @param id Tag id
     * @return TagDto that corresponds to the specified id
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDto getTag(@PathVariable("id") long id) {
        LOGGER.info("New get tag request");
        LOGGER.info(TAG_ID_EQUAL + id);
        return tagService.read(id);
    }

    /**
     * Returns list of all saved in database tags
     *
     * @return list of all saved tags
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TagDto> getAll() {
        return tagService.getAll();
    }

    /**
     * Creates new tag. If tag with such name already exists, exception would be thrown
     *
     * @param tagDto TagDto to be saved. Should contain unique tag name
     * @return TagDto that was created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto createTag(@RequestBody @NotNull @Valid TagDto tagDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.error("Create tag: unsuccessful binding");
            throw new InvalidRequestFormatException(bindingResult.toString());
        }

        LOGGER.info("New create tag request");
        LOGGER.info(TAG_ID_EQUAL + tagDto);
        tagService.create(tagDto);
        return tagDto;
    }

    /**
     * Updates tag by specified id
     *
     * @param tagDto TagDto, which should contain new tag name.
     * @param id     id of tag that should be updated
     * @return Updated TagDto
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDto updateTag(@RequestBody @Valid TagDto tagDto, @PathVariable Long id, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.error("Update tag: unsuccessful binding");
            throw new InvalidRequestFormatException(bindingResult.toString());
        }

        LOGGER.info("New update tag request");
        LOGGER.info(TAG_ID_EQUAL + id);
        LOGGER.info("Updated tag = " + tagDto);
        tagDto.setId(id);
        tagService.update(tagDto);
        return tagDto;
    }

    /**
     * Delete tag by id
     *
     * @param id id of the tag to be deleted
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTag(@PathVariable long id) {
        LOGGER.info("New delete tag request");
        LOGGER.info(TAG_ID_EQUAL + id);
        tagService.delete(id);
    }
}
