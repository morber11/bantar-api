package com.bantar.model;

import java.util.List;

public class ResponseDTO<T> {
    private String text;
    private long id;
    private List<T> categories;

    public ResponseDTO() {}

    public ResponseDTO(String text, long id) {
        this.text = text;
        this.id = id;
        this.categories = null;
    }

    public ResponseDTO(String text, long id, List<T> categories) {
        this.text = text;
        this.id = id;
        this.categories = categories;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<T> getCategories() {
        return categories;
    }

    public void setCategories(List<T> categories) {
        this.categories = categories;
    }
}
