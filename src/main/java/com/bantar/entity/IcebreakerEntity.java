package com.bantar.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ICEBREAKER")
public class IcebreakerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 500)
    private String text;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<com.bantar.entity.IcebreakerCategoryEntity> categories;

    public IcebreakerEntity() {
    }

    public IcebreakerEntity(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public IcebreakerEntity(long id, String text, List<com.bantar.entity.IcebreakerCategoryEntity> categories) {
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

    public List<com.bantar.entity.IcebreakerCategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<com.bantar.entity.IcebreakerCategoryEntity> categories) {
        this.categories = categories;
    }
}