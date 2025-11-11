package com.bantar.model;

import java.util.List;

public class Icebreaker {
    private String text;
    private long id;
    private List<IcebreakerCategory> categories;

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    public List<IcebreakerCategory> getCategories() {
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

    public void setCategories(List<IcebreakerCategory> categories) {
        this.categories = categories;
    }

    @SuppressWarnings("unused")
    public Icebreaker() {

    }

    public Icebreaker(String text, long id) {
        this.text = text;
        this.id = id;
        this.categories = null;
    }
}