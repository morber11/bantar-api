package com.bantar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DEBATE_CATEGORY")
public class DebateCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long debateCategoryId;

    @Column(name = "CATEGORY_CODE", length = 100)
    private String categoryCode;

    @ManyToOne
    @JoinColumn(name = "DEBATE_ID", nullable = false)
    private DebateEntity debate;

    public DebateCategoryEntity() {
    }

    public DebateCategoryEntity(long debateCategoryId, String categoryCode, DebateEntity debate) {
        this.debateCategoryId = debateCategoryId;
        this.categoryCode = categoryCode;
        this.debate = debate;
    }

    @SuppressWarnings("unused")
    public long getDebateCategoryId() {
        return debateCategoryId;
    }

    @SuppressWarnings("unused")
    public void setDebateCategoryId(long debateCategoryId) {
        this.debateCategoryId = debateCategoryId;
    }

    public String getCategory() {
        return categoryCode;
    }

    public void setCategory(String category) {
        this.categoryCode = category;
    }

    public DebateEntity getDebate() {
        return debate;
    }

    @SuppressWarnings("unused")
    public void setDebate(DebateEntity debate) {
        this.debate = debate;
    }
}
