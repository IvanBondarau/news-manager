package com.epam.lab.controller;

import com.epam.lab.dto.TagDto;
import com.epam.lab.exception.ResourceAlreadyExistException;
import com.epam.lab.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping(value = "/tag/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDto getTag(@PathVariable("id") long id) {
        return tagService.read(id);
    }

    @PostMapping(value = "/tag")
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto createTag(@RequestBody TagDto tagDto) {
        tagService.create(tagDto);
        return tagDto;
    }

    @PutMapping(value = "/tag/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDto updateTag(@RequestBody TagDto tagDto, @PathVariable long id) {
        tagDto.setId(id);
        tagService.update(tagDto);
        return tagDto;
    }

    @DeleteMapping(value = "/tag/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTag(@PathVariable long id) {
        tagService.delete(id);
    }
}
