package com.epam.lab.dao;

import com.epam.lab.model.News;

import java.util.List;

public interface NewsDao extends CrudDao<News> {
    long getAuthorIdByNews(long id);
    void setNewsAuthor(long newsId, long authorId);
    void deleteNewsAuthor(long newsId);

    List<Long> getTagsIdForNews(long id);
    void setNewsTag(long newsId, long tagId);
    void deleteNewsTag(long newsId, long tagId);
}
