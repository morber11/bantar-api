package com.bantar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MIND_READER_CATEGORY")
public class MindReaderCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "CATEGORY_CODE", length = 100, nullable = false)
    private String categoryCode;

    @ManyToOne
    @JoinColumn(name = "MIND_READER_ID", nullable = false)
    private MindReaderEntity mindReader;

    @SuppressWarnings("unused")
    public MindReaderCategoryEntity() {
    }

    public MindReaderCategoryEntity(long id, String categoryCode, MindReaderEntity mindReader) {
        this.id = id;
        this.categoryCode = categoryCode;
        this.mindReader = mindReader;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    @SuppressWarnings("unused")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @SuppressWarnings("unused")
    public MindReaderEntity getMindReader() {
        return mindReader;
    }

    @SuppressWarnings("unused")
    public void setMindReader(MindReaderEntity mindReader) {
        this.mindReader = mindReader;
    }
}
