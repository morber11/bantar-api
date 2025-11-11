package com.bantar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ICEBREAKER_CATEGORY")
public class IcebreakerCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionCategoryId;

    @Column(name = "CATEGORY_CODE", length = 100)
    private String categoryCode;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID", nullable = false)
    private IcebreakerEntity question;

    public IcebreakerCategoryEntity() {
    }

    public IcebreakerCategoryEntity(long questionCategoryId, String categoryCode, IcebreakerEntity question) {
        this.questionCategoryId = questionCategoryId;
        this.categoryCode = categoryCode;
        this.question = question;
    }

    @SuppressWarnings("unused")
    public long getQuestionCategoryId() {
        return questionCategoryId;
    }

    @SuppressWarnings("unused")
    public void setQuestionCategoryId(long questionCategoryId) {
        this.questionCategoryId = questionCategoryId;
    }

    public String getCategory() {
        return categoryCode;
    }

    public void setCategory(String category) {
        this.categoryCode = category;
    }

    public IcebreakerEntity getQuestion() {
        return question;
    }

    public void setQuestion(IcebreakerEntity question) {
        this.question = question;
    }
}