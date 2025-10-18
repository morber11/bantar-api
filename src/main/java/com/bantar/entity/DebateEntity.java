package com.bantar.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "DEBATE")
public class DebateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 500)
    private String text;

    @OneToMany(mappedBy = "debate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DebateCategoryEntity> categories;

    public DebateEntity() {
    }

    public DebateEntity(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public DebateEntity(long id, String text, List<DebateCategoryEntity> categories) {
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

    public List<DebateCategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<DebateCategoryEntity> categories) {
        this.categories = categories;
    }
}
