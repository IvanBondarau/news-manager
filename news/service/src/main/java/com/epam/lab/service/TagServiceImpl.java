package com.epam.lab.service;

import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.TagDto;
import com.epam.lab.dto.converter.TagConverter;
import com.epam.lab.exception.TagAlreadyExistsException;
import com.epam.lab.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private TagDao tagDao;

    private TagConverter tagConverter;

    @Autowired
    public TagServiceImpl(TagDao tagDao, TagConverter tagConverter) {
        this.tagDao = tagDao;
        this.tagConverter = tagConverter;
    }

    @Override
    @Transactional
    public void create(TagDto dto) {

        Optional<Tag> searchResult = tagDao.findByName(dto.getName());
        if (searchResult.isPresent()) {
            dto.setId(searchResult.get().getId());
            throw new TagAlreadyExistsException(dto.getId(), searchResult.get().getName());
        } else {
            Tag entity = tagConverter.convertToEntity(dto);
            tagDao.create(entity);
            dto.setId(entity.getId());
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
        tagDao.delete(id);
    }

    @Override
    @Transactional
    public List<TagDto> getAll() {
        return tagDao.getAll().stream()
                .map(author -> tagConverter.convertToDto(author))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void save(TagDto dto) {
        Optional<Tag> searchResult = tagDao.findByName(dto.getName());
        if (searchResult.isPresent()) {
            dto.setId(searchResult.get().getId());
        } else {
            Tag tag = tagConverter.convertToEntity(dto);
            tagDao.create(tag);
            dto.setId(tag.getId());
        }
    }


}
