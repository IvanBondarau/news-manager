package com.epam.lab.service;

import com.epam.lab.dto.NewsDto;

import java.util.Comparator;

public class NewsAuthorComparator implements Comparator<NewsDto> {

    @Override
    public int compare(NewsDto o1, NewsDto o2) {
        if (o1.getAuthor() == o2.getAuthor()) {
            return 0;
        }
        if (o1.getAuthor() == null) {
            return -1;
        }
        if (o2.getAuthor() == null) {
            return 1;
        }
        return (o1.getAuthor().toString().compareTo(o2.getAuthor().toString()));

    }
}
