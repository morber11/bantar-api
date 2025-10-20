package com.bantar.model;

import java.util.List;

public class Debate {
    private String text;
    private long id;
    private List<DebateCategory> categories;

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    public List<DebateCategory> getCategories() {
        return categories;
    }

    @SuppressWarnings("unused")
    public void setText(String text) {
        this.text = text;
    }

    @SuppressWarnings("unused")
    public void setId(long id) {
        this.id = id;
    }

    public void setCategories(List<DebateCategory> categories) {
        this.categories = categories;
    }

    @SuppressWarnings("unused")
    public Debate() {

    }

    public Debate(String text, long id) {
        this.text = text;
        this.id = id;
        this.categories = null;
    }
}