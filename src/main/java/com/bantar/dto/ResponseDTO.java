package com.bantar.dto;

import java.util.List;

public record ResponseDTO<T>(String text, long id, List<T> categories) {

    public ResponseDTO(String text, long id) {
        this(text, id, null);
    }

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    public List<T> getCategories() {
        return categories;
    }
}
