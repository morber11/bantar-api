package com.bantar.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;


import java.util.List;

@Entity
@Table(name = "QUESTION")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 500)
    private String text;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuestionCategoryEntity> categories;

    public QuestionEntity() {}

    public QuestionEntity(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public QuestionEntity(long id, String text, List<QuestionCategoryEntity> categories) {
        this.id = id;
        this.text = text;
        this.categories = categories;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<QuestionCategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<QuestionCategoryEntity> categories) {
        this.categories = categories;
    }
}