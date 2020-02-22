package com.epam.lab.dao;

import com.epam.lab.exception.DataEntityNotFoundException;
import com.epam.lab.model.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class TagHibernateDao implements TagDao {

    private static final Logger LOGGER = Logger.getLogger(TagHibernateDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Optional<Tag> findByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> query = cb.createQuery(Tag.class);
        Root<Tag> tagRoot = query.from(Tag.class);

        Predicate predicate = cb.equal(tagRoot.get(Tag_.NAME), name);
        query.select(tagRoot)
                .where(predicate);
        TypedQuery<Tag> tagTypedQuery = entityManager.createQuery(query);
        return tagTypedQuery.getResultList().stream().findFirst();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Long> findNewsIdByTagNames(Set<String> tagNames) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<News> newsRoot = query.from(News.class);

        Join<News, Tag> newsTagJoin = newsRoot.join(News_.TAGS);

        query.multiselect(newsRoot.get("id"))
                .where(cb.in(newsTagJoin.get(Tag_.NAME)).value(tagNames))
                .groupBy(newsRoot.get(News_.ID))
                .having(cb.greaterThanOrEqualTo(cb.count(newsRoot), (long) tagNames.size()));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        LOGGER.info(typedQuery.getResultList());
        return typedQuery.getResultList()
                .stream()
                .map((Tuple t) -> (Long) t.get(0))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Tag> getAll() {
        CriteriaQuery<Tag> getAllQuery = entityManager.getCriteriaBuilder().createQuery(Tag.class);
        Root<Tag> root = getAllQuery.from(Tag.class);
        getAllQuery = getAllQuery.select(root);

        TypedQuery<Tag> query = entityManager.createQuery(getAllQuery);
        return query.getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void create(Tag entity) {
        entityManager.persist(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Tag read(long id) {
        Tag result = entityManager.find(Tag.class, id);
        if (result == null) {
            throw new DataEntityNotFoundException(EntityType.TAG, id);
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(Tag entity) {
        Tag loaded = entityManager.find(Tag.class, entity.getId());
        if (loaded == null) {
            throw new DataEntityNotFoundException(EntityType.TAG, entity.getId());
        }
        entityManager.merge(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long id) {
        Tag loaded = entityManager.find(Tag.class, id);
        if (loaded == null) {
            throw new DataEntityNotFoundException(EntityType.TAG, id);
        }
        entityManager.remove(loaded);
    }
}
