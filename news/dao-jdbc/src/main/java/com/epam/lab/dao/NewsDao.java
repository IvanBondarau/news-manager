package com.epam.lab.dao;

import com.epam.lab.model.News;

import java.util.List;

public interface NewsDao extends CrudDao<News> {
    long getAuthorIdByNews(News news);
    void setNewsAuthor(News news, long authorId);
    void deleteNewsAuthor(News news);

    List<Long> getTagsIdForNews(News news);
    void setNewsTag(News news, long tagId);
    void deleteNewsTag(News news, long tagId);
}
