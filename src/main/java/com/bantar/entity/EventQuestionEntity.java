package com.bantar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "EVENT_QUESTIONS")
public class EventQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 500)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    private EventEntity event;

    public EventQuestionEntity(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public EventQuestionEntity() {
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

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }
}
