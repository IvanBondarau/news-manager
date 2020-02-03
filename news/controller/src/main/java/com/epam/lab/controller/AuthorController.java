package com.epam.lab.controller;

import com.epam.lab.dto.AuthorDto;
import com.epam.lab.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping(value = "/author/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDto getAuthor(@PathVariable("id") long id) {
        return authorService.read(id);
    }

    @PostMapping(value = "/author")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDto createAuthor(@RequestBody AuthorDto authorDto) {
        authorService.create(authorDto);
        return authorDto;
    }

    @PutMapping(value = "/author/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDto updateAuthor(@RequestBody AuthorDto authorDto, @PathVariable long id) {
        authorDto.setId(id);
        authorService.update(authorDto);
        return authorDto;
    }

    @DeleteMapping(value = "/author/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAuthor(@PathVariable long id) {
        authorService.delete(id);
    }
}
