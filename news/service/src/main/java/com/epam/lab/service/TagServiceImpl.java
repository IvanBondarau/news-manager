package com.epam.lab.service;

import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.TagDto;
import com.epam.lab.exception.ResourceAlreadyExistException;
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

    @Autowired
    public TagServiceImpl(TagDao tagDao, NewsDao newsDao) {
        this.tagDao = tagDao;
        this.newsDao = newsDao;
    }



    @Override
    @Transactional
    public void create(TagDto dto) {
        Optional<Tag> searchResult = tagDao.findByName(dto.getName());
        if (searchResult.isPresent()) {
            throw new ResourceAlreadyExistException(dto.toString() + " already exist", searchResult.get().getId());
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
    public void delete(long id) {

        List<Long> dependencies = newsDao.getTagsIdForNews(id);

        for (Long newsId: dependencies) {
            newsDao.deleteNewsTag(newsId, id);
        }

        tagDao.delete(id);
    }


}
