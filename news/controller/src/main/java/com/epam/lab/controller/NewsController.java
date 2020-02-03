package com.epam.lab.controller;

import com.epam.lab.dto.NewsDto;
import com.epam.lab.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class NewsController {
    @Autowired
    private NewsService newsService;

    @GetMapping(value = "/news/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsDto getNews(@PathVariable("id") long id) {
        return newsService.read(id);
    }

    @PostMapping(value = "/news")
    @ResponseStatus(HttpStatus.CREATED)
    public NewsDto createNews(@RequestBody NewsDto newsDto) {
        newsService.create(newsDto);
        return newsDto;
    }

    @PutMapping(value = "/news/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsDto updateNews(@RequestBody NewsDto newsDto, @PathVariable long id) {
        newsDto.setId(id);
        newsService.update(newsDto);
        return newsDto;
    }

    @DeleteMapping(value = "/news/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteNews(@PathVariable long id) {
        newsService.delete(id);
    }
}
