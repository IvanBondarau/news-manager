package com.epam.lab.dto;

public enum SortOrder {
    BY_DATE,
    BY_AUTHOR,
    BY_TAGS;

    public static SortOrder fromString(String str) {
        if ("byDate".equals(str)) {
            return BY_DATE;
        } else if ("byAuthor".equals(str)) {
            return BY_AUTHOR;
        } else if ("byTags".equals(str)) {
            return BY_TAGS;
        } else {
            throw new IllegalArgumentException("No constant for str " + str + " found");
        }
    }
}
