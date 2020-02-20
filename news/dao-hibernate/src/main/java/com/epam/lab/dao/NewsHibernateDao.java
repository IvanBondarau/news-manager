package com.epam.lab.dao;

import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NewsHibernateDao implements NewsDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long getAuthorIdByNews(long id) {
        return entityManager.find(News.class, id).getAuthors().get(0).getId();
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Long> getTagsIdForNews(long id) {
        News news = entityManager.find(News.class, id);
        if (news == null) {
            throw new RuntimeException();
        }
        return news.getTags().stream().map(Tag::getId).collect(Collectors.toList());
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<News> getAll() {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<News> cq = qb.createQuery(News.class);
        cq.select(cq.from(News.class));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long count() {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(News.class)));
        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void create(News entity) {
        entityManager.persist(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public News read(long id) {
        News news = entityManager.find(News.class, id);
        if (news == null) {
            throw new RuntimeException();
        }
        return news;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(News entity) {
        News news = entityManager.find(News.class, entity.getId());
        if (news == null) {
            throw new RuntimeException();
        }
        entityManager.merge(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long id) {
        News news = entityManager.find(News.class, id);
        if (news == null) {
            throw new RuntimeException();
        }
        entityManager.remove(news);
    }
}
