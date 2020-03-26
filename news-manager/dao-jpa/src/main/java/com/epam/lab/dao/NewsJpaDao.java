package com.epam.lab.dao;

import com.epam.lab.exception.DataEntityNotFoundException;
import com.epam.lab.exception.NewsAuthorNotFoundException;
import com.epam.lab.model.Author;
import com.epam.lab.model.EntityType;
import com.epam.lab.model.News;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class NewsJpaDao implements NewsDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long getAuthorIdByNewsId(long id) {
        Set<Author> authorSet = entityManager.find(News.class, id).getAuthors();
        Optional<Author> author = authorSet.stream().findFirst();
        if (author.isPresent()) {
            return author.get().getId();
        } else {
            throw new NewsAuthorNotFoundException("Author for news with id " + id + " not found");
        }
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
            throw new DataEntityNotFoundException(EntityType.NEWS, id);
        }
        return news;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(News entity) {
        News news = entityManager.find(News.class, entity.getId());
        if (news == null) {
            throw new DataEntityNotFoundException(EntityType.NEWS, entity.getId());
        }
        entityManager.merge(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long id) {
        News news = entityManager.find(News.class, id);
        if (news == null) {
            throw new DataEntityNotFoundException(EntityType.NEWS, id);
        }
        entityManager.remove(news);
    }
}
