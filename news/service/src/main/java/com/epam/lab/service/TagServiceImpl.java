package com.epam.lab.service;

import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.TagConverter;
import com.epam.lab.dto.TagDto;
import com.epam.lab.exception.TagAlreadyExistException;
import com.epam.lab.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    private TagDao tagDao;
    private NewsDao newsDao;

    private TagConverter tagConverter;

    @Autowired
    public TagServiceImpl(TagDao tagDao, NewsDao newsDao, TagConverter tagConverter) {
        this.tagDao = tagDao;
        this.newsDao = newsDao;
        this.tagConverter = tagConverter;
    }

    @Override
    @Transactional
    public void create(TagDto dto) {
        Optional<Tag> searchResult = tagDao.findByName(dto.getName());
        if (searchResult.isPresent()) {
            dto.setId(searchResult.get().getId());
            throw new TagAlreadyExistException(dto.getId(), searchResult.get().getName());
        } else {
            long id = tagDao.create(new Tag(dto.getName()));
            dto.setId(id);
        }
    }

    @Override
    @Transactional
    public TagDto read(long id) {
        Tag tag = tagDao.read(id);
        return tagConverter.convertToDto(tag);
    }

    @Override
    @Transactional
    public void update(TagDto dto) {
        Tag entity = tagConverter.convertToEntity(dto);
        tagDao.update(entity);
    }

    @Override
    @Transactional
    public void delete(long id) {

        List<Long> dependencies = newsDao.getTagsIdForNews(id);

        for (Long newsId : dependencies) {
            newsDao.deleteNewsTag(newsId, id);
        }

        tagDao.delete(id);
    }

    @Override
    public void loadOrCreateTag(TagDto dto) {
        Optional<Tag> searchResult = tagDao.findByName(dto.getName());
        if (searchResult.isPresent()) {
            dto.setId(searchResult.get().getId());
        } else {
            long id = tagDao.create(new Tag(dto.getName()));
            dto.setId(id);
        }
    }



}
