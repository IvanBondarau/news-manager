package com.epam.lab.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class TagDto implements Dto {

    private long id;

    @NotNull
    @Size(max = 20)
    private String name;

    public TagDto() {
        id = Long.MIN_VALUE;
    }

    public TagDto(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        TagDto tagDto = (TagDto) o;
        return Objects.equals(name, tagDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
