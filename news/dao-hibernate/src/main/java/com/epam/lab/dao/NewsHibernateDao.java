package com.epam.lab.dao;

import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NewsHibernateDao implements NewsDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public long getAuthorIdByNews(long id) {
        return entityManager.find(News.class, id).getAuthors().get(0).getId();
    }

    @Override
    public void setNewsAuthor(long newsId, long authorId) {
        News news = entityManager.find(News.class, newsId);
        Author author = entityManager.find(Author.class, authorId);
        if (news == null) {
            throw new RuntimeException();
        }
        if (author == null) {
            throw new RuntimeException();
        }
        if (news.getAuthors().size() != 0) {
            throw new RuntimeException();
        }
        news.getAuthors().add(author);
        entityManager.merge(news);
    }

    @Override
    public void deleteNewsAuthor(long newsId) {
        News news = entityManager.find(News.class, newsId);
        if (news.getAuthors().size() == 0) {
            throw new RuntimeException();
        }
        news.getAuthors().clear();
        entityManager.merge(news);
    }

    @Override
    public List<Long> getTagsIdForNews(long id) {
        News news = entityManager.find(News.class, id);
        if (news ==  null) {
            throw new RuntimeException();
        }
        return news.getTags().stream().map(Tag::getId).collect(Collectors.toList());
    }

    @Override
    public void setNewsTag(long newsId, long tagId) {
        News news = entityManager.find(News.class, newsId);
        Tag tag = entityManager.find(Tag.class, tagId);
        if (news == null) {
            throw new RuntimeException();
        }
        if (tag == null) {
            throw new RuntimeException();
        }
        news.getTags().add(tag);
        entityManager.merge(news);
    }

    @Override
    public void deleteNewsTag(long newsId, long tagId) {
        News news = entityManager.find(News.class, newsId);
        Tag tag = entityManager.find(Tag.class, tagId);
        if (news == null) {
            throw new RuntimeException();
        }
        if (tag == null) {
            throw new RuntimeException();
        }
        news.getTags().remove(tag);
        entityManager.merge(news);
    }

    @Override
    public List<News> getAll() {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<News> cq = qb.createQuery(News.class);
        cq.select(cq.from(News.class));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public Long count() {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(News.class)));
        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public void create(News entity) {
        entityManager.persist(entity);
    }

    @Override
    public News read(long id) {
        News news = entityManager.find(News.class, id);
        if (news == null) {
            throw new RuntimeException();
        }
        return news;
    }

    @Override
    public void update(News entity) {
        News news = entityManager.find(News.class, entity.getId());
        if (news == null) {
            throw new RuntimeException();
        }
        entityManager.merge(entity);
    }

    @Override
    public void delete(long id) {
        News news = entityManager.find(News.class, id);
        if (news == null) {
            throw new RuntimeException();
        }
        entityManager.remove(news);
    }
}
