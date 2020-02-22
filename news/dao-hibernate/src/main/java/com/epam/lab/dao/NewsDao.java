package com.epam.lab.dao;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

public interface NewsDao extends CrudDao<News> {

    long getAuthorIdByNewsId(long id);

    List<News> getAll();
    Long count();

}
