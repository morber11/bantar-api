package com.bantar.model;

public class Question {
    private String text;
    private int id;

    public String getText() {
        return text;
    }
    public int getId() { return id; }

    @SuppressWarnings("unused")
    public void setText(String text) { this.text = text; }
    @SuppressWarnings("unused")
    public void setId(int id) { this.id = id; }

    @SuppressWarnings("unused")
    public Question() {

    }

    public Question(String text, int id) {
        this.text = text;
        this.id = id;
    }
}
