package com.epam.lab.dto;

import java.util.HashMap;
import java.util.Map;

public enum SortOrder {
    BY_DATE,
    BY_AUTHOR,
    BY_TAGS;

    private static final Map<String, SortOrder> orderNames = new HashMap<>();

    static {
        orderNames.put("byDate", BY_DATE);
        orderNames.put("byAuthor", BY_AUTHOR);
        orderNames.put("byTags", BY_TAGS);
    }

    public static SortOrder fromString(String str) {
        if (orderNames.containsKey(str)) {
            return orderNames.get(str);
        } else {
            throw new IllegalArgumentException("Constant for string " + str + " not found");
        }
    }
}
