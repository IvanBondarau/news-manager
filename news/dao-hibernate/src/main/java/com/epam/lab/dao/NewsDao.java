package com.epam.lab.dao;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

public interface NewsDao extends CrudDao<News> {
    long getAuthorIdByNews(long id);
    void setNewsAuthor(long newsId, long authorId);
    void deleteNewsAuthor(long newsId);

    List<Long> getTagsIdForNews(long id);
    void setNewsTag(long newsId, long tagId);
    void deleteNewsTag(long newsId, long tagId);

    List<News> getAll();
    Long count();

}
