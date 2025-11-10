package com.bantar.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "MIND_READER")
public class MindReaderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 500, nullable = false)
    private String text;

    @OneToMany(mappedBy = "mindReader", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MindReaderCategoryEntity> categories;

    public MindReaderEntity(long id, String text) {
        this.id = id;
        this.text = text;
    }

    @SuppressWarnings("unused")
    public MindReaderEntity(long id, String text, List<MindReaderCategoryEntity> categories) {
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

    public List<MindReaderCategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<MindReaderCategoryEntity> categories) {
        this.categories = categories;
    }
}
