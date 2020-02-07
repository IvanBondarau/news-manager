package com.epam.lab.service;

import com.epam.lab.dto.NewsDto;
import com.epam.lab.dto.TagDto;

import java.util.Comparator;
import java.util.stream.Collectors;

public class NewsTagComparator implements Comparator<NewsDto> {

    @Override
    public int compare(NewsDto o1, NewsDto o2) {
        if (o1.getTags() == o2.getTags()) {
            return 0;
        }
        if (o1.getTags() == null) {
            return -1;
        }
        if (o2.getTags() == null) {
            return 1;
        }
        String tags1 = o1.getTags().stream().map((TagDto::getName)).sorted().collect(Collectors.joining(","));
        String tags2 = o2.getTags().stream().map((TagDto::getName)).sorted().collect(Collectors.joining(","));

        return tags1.compareTo(tags2);

    }
}