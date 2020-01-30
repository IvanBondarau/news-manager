package com.epam.lab.service;

import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.TagDto;
import com.epam.lab.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    private TagDao tagDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    @Transactional
    public void create(TagDto dto) {
        Optional<Tag> searchResult = tagDao.findByName(dto.getName());
        if (searchResult.isPresent()) {
            dto.setId(searchResult.get().getId());
        } else {
            long id = tagDao.create(new Tag(dto.getName()));
            dto.setId(id);
        }
    }

    @Override
    @Transactional
    public TagDto read(long id) {
        Tag loaded = tagDao.read(id);
        TagDto dto = new TagDto();
        dto.setId(loaded.getId());
        dto.setName(loaded.getName());
        return dto;
    }

    @Override
    @Transactional
    public void update(TagDto dto) {
        Tag entity = new Tag(dto.getId(), dto.getName());
        tagDao.update(entity);
    }

    @Override
    @Transactional
    public void delete(TagDto dto) {
        long id = dto.getId();
        tagDao.delete(id);
    }
}
