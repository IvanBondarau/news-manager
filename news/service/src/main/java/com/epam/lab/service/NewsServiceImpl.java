package com.epam.lab.service;

import com.epam.lab.dao.AuthorDao;
import com.epam.lab.dao.NewsDao;
import com.epam.lab.dao.TagDao;
import com.epam.lab.dto.AuthorDto;
import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.TagDto;
import com.epam.lab.model.Author;
import com.epam.lab.model.News;
import com.epam.lab.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private NewsDao newsDao;
    private AuthorDao authorDao;
    private TagDao tagDao;

    @Autowired
    public NewsServiceImpl(NewsDao newsDao, AuthorDao authorDao, TagDao tagDao) {
        this.newsDao = newsDao;
        this.authorDao = authorDao;
        this.tagDao = tagDao;
    }

    @Override
    public void create(NewsDto dto) {
        News entity = buildNewsFromDto(dto);

        long id = newsDao.create(entity);
        dto.setId(id);

        newsDao.setNewsAuthor(dto.getId(), dto.getAuthor().getId());

        for (TagDto tagDto : dto.getTags()) {
            createOrGet(tagDto);
            newsDao.setNewsTag(dto.getId(), tagDto.getId());
        }
    }

    @Override
    public NewsDto read(long id) {
        News entity = newsDao.read(id);

        NewsDto dto = new NewsDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setShortText(entity.getShortText());
        dto.setFullText(entity.getFullText());
        dto.setCreationDate(entity.getCreationDate());
        dto.setModificationDate(entity.getModificationDate());

        long authorId = newsDao.getAuthorIdByNews(dto.getId());
        Author author = authorDao.read(authorId);
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(authorId);
        authorDto.setName(author.getName());
        authorDto.setSurname(author.getSurname());

        dto.setAuthor(authorDto);

        List<Long> tagIds = newsDao.getTagsIdForNews(dto.getId());

        dto.setTags(tagIds.stream().map((Long tagId) -> {
            Tag tag = tagDao.read(tagId);
            TagDto tagDto = new TagDto();
            tagDto.setId(tag.getId());
            tagDto.setName(tag.getName());
            return tagDto;
        }).collect(Collectors.toSet()));

        return dto;
    }

    @Override
    public void update(NewsDto dto) {

        NewsDto oldDto = read(dto.getId());

        if (!oldDto.getAuthor().equals(dto.getAuthor())) {
            newsDao.deleteNewsAuthor(dto.getId());
            newsDao.setNewsAuthor(dto.getId(), dto.getAuthor().getId());
        }

        updateTags(oldDto, dto);

        News entity = buildNewsFromDto(dto);
        entity.setId(dto.getId());
        newsDao.update(entity);
    }

    @Override
    public void delete(long id) {
        newsDao.delete(id);
    }

    @Transactional
    private void createOrGet(TagDto dto) {
        Optional<Tag> searchResult = tagDao.findByName(dto.getName());
        if (searchResult.isPresent()) {
            dto.setId(searchResult.get().getId());
        } else {
            long id = tagDao.create(new Tag(dto.getName()));
            dto.setId(id);
        }
    }

    private News buildNewsFromDto(NewsDto dto) {
        return new News(
                dto.getTitle(),
                dto.getShortText(),
                dto.getFullText(),
                dto.getCreationDate(),
                dto.getModificationDate()
        );
    }

    @Transactional
    private void updateTags(NewsDto oldDto, NewsDto dto) {
        for (TagDto tagDto : dto.getTags()) {
            createOrGet(tagDto);
        }

        Set<TagDto> tagsToRemove = new HashSet<>(oldDto.getTags());
        tagsToRemove.removeAll(dto.getTags());

        for (TagDto tagDto : tagsToRemove) {
            newsDao.deleteNewsTag(oldDto.getId(), tagDto.getId());
        }

        Set<TagDto> tagsToAdd = new HashSet<>(dto.getTags());
        tagsToAdd.removeAll(oldDto.getTags());

        for (TagDto tagDto : tagsToAdd) {
            newsDao.setNewsTag(oldDto.getId(), tagDto.getId());
        }
    }

}
