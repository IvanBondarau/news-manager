package com.epam.lab.dao;

import com.epam.lab.exception.DataEntityNotFoundException;
import com.epam.lab.model.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AuthorHibernateDao implements AuthorDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void create(Author entity) {
        this.entityManager.persist(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Author read(long id) {
        Author author = this.entityManager.find(Author.class, id);
        if (author == null) {
            throw new DataEntityNotFoundException(EntityType.AUTHOR, id);
        }
        return author;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void update(Author entity) {
        Author old = this.entityManager.find(Author.class, entity.getId());
        if (old != null) {
            this.entityManager.merge(entity);
        } else {
            throw new DataEntityNotFoundException(EntityType.AUTHOR, entity.getId());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long id) {
        Author author = this.entityManager.find(Author.class, id);
        if (author == null) {
            throw new DataEntityNotFoundException(EntityType.AUTHOR, id);
        }
        this.entityManager.remove(author);
    }

    @Transactional
    public List<Long> findNewsByAuthorId(long authorId) {
        Author author = entityManager.find(Author.class, authorId);
        if (author == null) {
            throw new DataEntityNotFoundException(EntityType.AUTHOR, authorId);
        }
        return author.getNews().stream()
                .map(News::getId)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Long> findNewsByAuthorName(String authorName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<Author> authorRoot = query.from(Author.class);
        Join<Author, News> join = authorRoot.join(Author_.NEWS);

        query.multiselect(join.get(News_.ID))
                .where(criteriaBuilder.equal(authorRoot.get(Author_.NAME), authorName))
                .groupBy(join.get(News_.ID));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream()
                .map(tuple -> (Long) tuple.get(0))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Long> findNewsByAuthorSurname(String authorSurname) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<Author> authorRoot = query.from(Author.class);
        Join<Author, News> join = authorRoot.join(Author_.NEWS);

        query.multiselect(join.get(News_.ID))
                .where(criteriaBuilder.equal(authorRoot.get(Author_.SURNAME), authorSurname))
                .groupBy(join.get(News_.ID));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream()
                .map(tuple -> (Long) tuple.get(0))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Author> getAll() {
        CriteriaQuery<Author> getAllQuery = entityManager.getCriteriaBuilder().createQuery(Author.class);
        Root<Author> root = getAllQuery.from(Author.class);
        getAllQuery = getAllQuery.select(root);

        TypedQuery<Author> query = entityManager.createQuery(getAllQuery);
        return query.getResultList();
    }
}
