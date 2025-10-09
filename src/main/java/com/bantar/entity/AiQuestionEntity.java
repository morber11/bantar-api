package com.bantar.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "AI_QUESTION")
public class AiQuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 500, nullable = false)
    private String text;

    @Column(length = 64, nullable = false, unique = true)
    private String hash;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @SuppressWarnings("unused")
    public AiQuestionEntity() {
        this.createdAt = Instant.now();
    }

    public AiQuestionEntity(String text, String hash) {
        this.text = text;
        this.hash = hash;
        this.createdAt = Instant.now();
    }

    @SuppressWarnings("unused")
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
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

    public String getHash() {
        return hash;
    }

    @SuppressWarnings("unused")
    public void setHash(String hash) {
        this.hash = hash;
    }

    @SuppressWarnings("unused")
    public Instant getCreatedAt() {
        return createdAt;
    }

    @SuppressWarnings("unused")
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
