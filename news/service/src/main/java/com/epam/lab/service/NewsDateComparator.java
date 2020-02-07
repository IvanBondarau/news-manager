package com.epam.lab.service;


import com.epam.lab.dto.NewsDto;

import java.util.Comparator;

public class NewsDateComparator implements Comparator<NewsDto> {
    @Override
    public int compare(NewsDto o1, NewsDto o2) {
        if (o1.getCreationDate() == o2.getCreationDate()) {
            return 0;
        }
        if (o1.getCreationDate() == null) {
            return -1;
        }
        if (o2.getCreationDate() == null) {
            return 1;
        }
        return o1.getCreationDate().compareTo(o2.getCreationDate());
    }
}