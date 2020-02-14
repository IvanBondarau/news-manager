package com.epam.lab.dao;

import com.epam.lab.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagDao extends CrudDao<Tag> {
    List<Long> getNewsIdByTag(Tag tag);
    Optional<Tag> findByName(String name);

    List<Long> findNewsIdByTagNames(Set<String> tagNames);

    List<Tag> getAll();
}
