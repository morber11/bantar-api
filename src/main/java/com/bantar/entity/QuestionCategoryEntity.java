package com.bantar.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "QUESTION_CATEGORY")
public class QuestionCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionCategoryId;

    @Column(name = "CATEGORY_CODE", length = 100)
    private String categoryCode;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID", nullable = false)
    private QuestionEntity question;

    public QuestionCategoryEntity() {}

    public QuestionCategoryEntity(long questionCategoryId, String categoryCode, QuestionEntity question) {
        this.questionCategoryId = questionCategoryId;
        this.categoryCode = categoryCode;
        this.question = question;
    }

    public long getQuestionCategoryId() {
        return questionCategoryId;
    }

    public void setQuestionCategoryId(long questionCategoryId) {
        this.questionCategoryId = questionCategoryId;
    }

    public String getCategory() {
        return categoryCode;
    }

    public void setCategory(String category) {
        this.categoryCode = category;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }
}