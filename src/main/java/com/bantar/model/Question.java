package com.bantar.model;

import java.util.List;

public class Question {
    private String text;
    private long id;
    private List<QuestionCategory> categories;

    public String getText() {
        return text;
    }
    public long getId() { return id; }
    public List<QuestionCategory> getCategories() { return categories; }

    @SuppressWarnings("unused")
    public void setText(String text) { this.text = text; }
    @SuppressWarnings("unused")
    public void setId(long id) { this.id = id; }
    public void setCategories(List<QuestionCategory> categories) { this.categories = categories; }

    @SuppressWarnings("unused")
    public Question() {

    }

    public Question(String text, long id) {
        this.text = text;
        this.id = id;
        this.categories = null;
    }

    public Question(String text, long id, List<QuestionCategory> categories) {
        this.text = text;
        this.id = id;
        this.categories = categories;
    }
}
