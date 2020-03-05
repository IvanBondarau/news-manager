package com.epam.lab.dao;

import com.epam.lab.model.News;

import java.util.List;

public interface NewsDao extends CrudDao<News> {

    long getAuthorIdByNewsId(long id);

    List<News> getAll();

    Long count();

}
