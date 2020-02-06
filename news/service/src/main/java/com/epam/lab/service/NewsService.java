package com.epam.lab.service;

import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.SearchCriteria;

import java.util.List;

public interface NewsService extends CrudService<NewsDto> {
    List<NewsDto> search(SearchCriteria searchCriteria);
}
