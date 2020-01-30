package com.epam.lab.dao;

import com.epam.lab.model.Tag;

import java.util.List;

public interface TagDao extends CrudDao<Tag> {
    List<Long> getNewsIdForTag(Tag tag);
}
