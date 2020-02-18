//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.epam.lab.dao;

import com.epam.lab.model.Author;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorHibernateDao implements AuthorDao {
    private static final String READ_ALL_QUERY = "SELECT author FROM Author AS author";

    private static final String SELECT_NEWS_ID_BY_AUTHOR = "SELECT news.id FROM News AS news JOIN news.authors AS author WHERE author.id = :id";

    private static final String SELECT_NEWS_ID_BY_AUTHOR_NAME = "SELECT news.id FROM News AS news JOIN news.authors AS author WHERE author.name = :name ";

    private static final String SELECT_NEWS_ID_BY_AUTHOR_SURNAME = "SELECT news.id FROM News AS news JOIN news.authors AS author WHERE author.surname = :surname ";

    @Autowired
    private EntityManager entityManager;

    public void create(Author entity) {
        this.entityManager.persist(entity);
    }

    public Author read(long id) {
        Author author = (Author)this.entityManager.find(Author.class, id);
        if (author == null) {
            throw new RuntimeException();
        } else {
            return author;
        }
    }

    public void update(Author entity) {
        Author old = (Author)this.entityManager.find(Author.class, entity.getId());
        if (old != null) {
            this.entityManager.merge(entity);
        } else {
            throw new RuntimeException();
        }
    }

    public void delete(long id) {
        Author author = (Author)this.entityManager.find(Author.class, id);
        this.entityManager.remove(author);
    }

    public List<Long> getNewsIdByAuthor(long authorId) {
        TypedQuery<Long> query = this.entityManager.createQuery(SELECT_NEWS_ID_BY_AUTHOR, Long.class);
        query.setParameter("id", authorId);
        return query.getResultList();
    }

    public List<Long> getNewsIdByAuthorName(String authorName) {
        TypedQuery<Long> query = this.entityManager.createQuery(SELECT_NEWS_ID_BY_AUTHOR_NAME, Long.class);
        query.setParameter("name", authorName);
        return query.getResultList();
    }

    public List<Long> getNewsIdByAuthorSurname(String authorSurname) {
        TypedQuery<Long> query = this.entityManager.createQuery(SELECT_NEWS_ID_BY_AUTHOR_SURNAME, Long.class);
        query.setParameter("surname", authorSurname);
        return query.getResultList();
    }

    public List<Author> getAll() {
        return this.entityManager.createQuery("SELECT author FROM Author AS author", Author.class).getResultList();
    }
}
