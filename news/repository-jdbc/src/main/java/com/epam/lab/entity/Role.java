package com.epam.lab.entity;

import java.util.Objects;

public class Role {

    private long userId;
    private String name;

    public Role(long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return userId == role.userId &&
                name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }
}
