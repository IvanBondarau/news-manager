package com.epam.lab.dao;

import com.epam.lab.model.Tag;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public interface TagDao extends CrudDao<Tag> {
    List<Long> getNewsIdForTag(Tag tag);
    Optional<Tag> findByName(String name);
}
