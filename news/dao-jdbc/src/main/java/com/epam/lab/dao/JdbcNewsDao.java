package com.epam.lab.dao;

import com.epam.lab.exception.AuthorAlreadyExistException;
import com.epam.lab.exception.AuthorNotFoundException;
import com.epam.lab.exception.NewsNotFoundException;
import com.epam.lab.exception.TagAlreadyExistException;
import com.epam.lab.exception.TagNotFoundException;
import com.epam.lab.model.News;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class JdbcNewsDao extends AbstractDao implements NewsDao {

    private static final String INSERT_STATEMENT =
            "INSERT INTO public.news(title, short_text, full_text, creation_date, modification_date) "
                    + "VALUES(?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID_STATEMENT =
            "SELECT id, title, short_text, full_text, creation_date, modification_date "
                    + "FROM public.news WHERE id = ?";

    private static final String UPDATE_BY_ID_STATEMENT =
            "UPDATE public.news " +
                    "SET title = ?, short_text = ?, full_text = ?, creation_date = ?, modification_date = ? " +
                    "WHERE id = ?";

    private static final String DELETE_BY_ID_STATEMENT =
            "DELETE FROM public.news WHERE id = ?";

    private static final String SELECT_NEWS_AUTHOR_ID_STATEMENT =
            "SELECT author_id FROM news_author WHERE news_id = ?";

    private static final String DELETE_NEWS_AUTHOR_STATEMENT =
            "DELETE FROM news_author WHERE news_id = ?";

    private static final String INSERT_NEWS_AUTHOR_STATEMENT =
            "INSERT INTO news_author(news_id, author_id) VALUES(?, ?)";

    private static final String SELECT_COUNT_NEWS_AUTHORS_STATEMENT =
            "SELECT COUNT(author_id) FROM news_author WHERE news_id = ?";

    private static final String SELECT_NEWS_TAGS_STATEMENT =
            "SELECT tag_id FROM news_tag WHERE news_id = ?";

    private static final String DELETE_NEWS_TAG_STATEMENT =
            "DELETE FROM news_tag WHERE news_id = ? AND tag_id = ?";

    private static final String INSERT_NEWS_TAG_STATEMENT =
            "INSERT INTO news_tag(news_id, tag_id) VALUES(?, ?)";

    private static final String SELECT_COUNT_NEWS_TAGS_STATEMENT =
            "SELECT COUNT(tag_id) FROM news_tag WHERE news_id = ? AND tag_id = ?";

    public JdbcNewsDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long create(News entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((Connection connection) -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT,
                            Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, entity.getTitle());
                    preparedStatement.setString(2, entity.getShortText());
                    preparedStatement.setString(3, entity.getFullText());
                    preparedStatement.setDate(4, entity.getCreationDate());
                    preparedStatement.setDate(5, entity.getModificationDate());
                    return preparedStatement;
                },
                keyHolder
        );
        long key = (long) keyHolder.getKey();
        entity.setId(key);
        return key;
    }

    @Override
    public News read(long id) {
        List<News> loadedNews = jdbcTemplate.query(SELECT_BY_ID_STATEMENT,
                new Object[]{id},
                new NewsRowMapper());

        if (loadedNews.size() == 0) {
            throw new NewsNotFoundException("News with id " + id + " not found", id);
        }
        return loadedNews.get(0);
    }

    @Override
    public void update(News entity) {
        long numOfUpdated = jdbcTemplate.update(UPDATE_BY_ID_STATEMENT,
                entity.getTitle(),
                entity.getShortText(),
                entity.getFullText(),
                entity.getCreationDate(),
                entity.getModificationDate(),
                entity.getId());

        if (numOfUpdated != 1) {
            throw new NewsNotFoundException("News with id " + entity.getId() + " not found", entity.getId());
        }
    }

    @Override
    public void delete(long id) {
        long numOfDeleted = jdbcTemplate.update(DELETE_BY_ID_STATEMENT, id);
        if (numOfDeleted != 1) {
            throw new NewsNotFoundException("News with id " + id + " not found", id);
        }
    }

    @Override
    public long getAuthorIdByNews(News news) {
        List<Long> authorId = jdbcTemplate.query(SELECT_NEWS_AUTHOR_ID_STATEMENT,
                new Object[]{news.getId()},
                ((resultSet, i) -> resultSet.getLong(1)));

        if (authorId.size() != 1) {
            throw new AuthorNotFoundException("Author for news with id " + news.getId() + " not found");
        }
        return authorId.get(0);
    }

    @Override
    public void setNewsAuthor(News news, long authorId) {
        long authors = countNewsAuthors(news);
        if (authors != 0) {
            throw new AuthorAlreadyExistException("Author of news already exists", news.getId());
        }
        jdbcTemplate.update(INSERT_NEWS_AUTHOR_STATEMENT, news.getId(), authorId);
    }

    @Override
    public void deleteNewsAuthor(News news) {
        long numOfDeleted = jdbcTemplate.update(DELETE_NEWS_AUTHOR_STATEMENT, news.getId());
        if (numOfDeleted != 1) {
            throw new AuthorNotFoundException("News author (news id" + news.getId() + ") not found");
        }
    }

    @Override
    public List<Long> getTagsIdForNews(News news) {
        return jdbcTemplate.query(SELECT_NEWS_TAGS_STATEMENT,
                new Object[]{news.getId()},
                (resultSet, i) -> resultSet.getLong(1));
    }


    @Override
    public void setNewsTag(News news, long tagId) {
        long tags = countNewsTags(news, tagId);
        if (tags != 0) {
            throw new TagAlreadyExistException("News tag already exists", news.getId(), tagId);
        }
        jdbcTemplate.update(INSERT_NEWS_TAG_STATEMENT, news.getId(), tagId);
    }

    @Override
    public void deleteNewsTag(News news, long tagId) {
        long numOfDeleted = jdbcTemplate.update(DELETE_NEWS_TAG_STATEMENT, news.getId(), tagId);
        if (numOfDeleted != 1) {
            throw new TagNotFoundException("News author (news id" + news.getId() + ") not found", tagId);
        }
    }

    private long countNewsAuthors(News news) {
        return jdbcTemplate.query(
                SELECT_COUNT_NEWS_AUTHORS_STATEMENT,
                new Object[]{news.getId()},
                ((resultSet, i) -> resultSet.getLong(1))
        ).get(0);
    }

    private long countNewsTags(News news, long tagId) {
        return jdbcTemplate.query(
                SELECT_COUNT_NEWS_TAGS_STATEMENT,
                new Object[]{news.getId(), tagId},
                ((resultSet, i) -> resultSet.getLong(1))
        ).get(0);
    }


    private static final class NewsRowMapper implements RowMapper<News> {
        @Override
        public News mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong(1);
            String title = resultSet.getString(2);
            String shortText = resultSet.getString(3);
            String fullText = resultSet.getString(4);
            Date creationDate = resultSet.getDate(5);
            Date modificationDate = resultSet.getDate(6);

            return new News(id, title, shortText, fullText, creationDate, modificationDate);
        }
    }

}
