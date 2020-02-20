package com.epam.lab.dao;

import com.epam.lab.model.News;
import com.epam.lab.model.News_;
import com.epam.lab.model.Tag;
import com.epam.lab.model.Tag_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TagHibernateDao implements TagDao {

    @PersistenceUnit(name = "com.epam.lab.dao")
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Long> getNewsIdByTag(Tag tag) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<News> newsRoot = query.from(News.class);
        ListJoin<News, Tag> tagListJoin  = newsRoot.joinList(News_.TAGS);

        query.multiselect(newsRoot.get(News_.ID), tagListJoin.get(News_.TAGS));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList()
                .stream()
                .filter((Tuple t) -> ((List<Tag>)t.get(1)).contains(tag))
                .map((Tuple t) -> (Long) t.get(0))
                .collect(Collectors.toList());
    }

    @Override
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
    public List<Long> findNewsIdByTagNames(Set<String> tagNames) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Subquery<Tag> subquery = query.subquery(Tag.class);
        Root<Tag> tagRoot = subquery.from(Tag.class);
        subquery.select(tagRoot)
                .where(cb.in(tagRoot.get(Tag_.NAME)).value(tagNames));

        Root<News> newsRoot = query.from(News.class);
        query.multiselect(newsRoot.get("id"))
                .where(cb.in(newsRoot.get(News_.TAGS)).value(subquery));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList()
                .stream()
                .map((Tuple t) -> (Long) t.get(0))
                .collect(Collectors.toList());
    }

    @Override
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
        //entityManager.getTransaction().commit();
        //entityManager.flush();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Tag read(long id) {
        Tag result = entityManager.find(Tag.class, id);
        if (result == null) {
            throw new RuntimeException();
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(Tag entity) {
        Tag loaded = entityManager.find(Tag.class, entity.getId());
        if (loaded == null) {
            throw new RuntimeException();
        }
        entityManager.merge(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long id) {
        Tag loaded = entityManager.find(Tag.class, id);
        if (loaded == null) {
            throw new RuntimeException();
        }
        entityManager.remove(loaded);
    }
}
