package com.epam.lab.model;

import java.util.EnumMap;

public enum  EntityType {
    AUTHOR,
    TAG,
    NEWS;

    private static final EnumMap<EntityType, String> enumNames
            = new EnumMap<>(EntityType.class);

    static {
        enumNames.put(EntityType.AUTHOR, "Author");
        enumNames.put(EntityType.NEWS, "News");
        enumNames.put(EntityType.TAG, "Tag");
    }

    @Override
    public String toString() {
        return enumNames.get(this);
    }
}
