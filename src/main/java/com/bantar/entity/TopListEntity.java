package com.bantar.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "TOPLIST")
public class TopListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 500)
    private String text;

    @OneToMany(mappedBy = "topList", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TopListCategoryEntity> categories;

    public TopListEntity() {
    }

    public TopListEntity(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public TopListEntity(long id, String text, List<TopListCategoryEntity> categories) {
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

    public List<TopListCategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<TopListCategoryEntity> categories) {
        this.categories = categories;
    }
}