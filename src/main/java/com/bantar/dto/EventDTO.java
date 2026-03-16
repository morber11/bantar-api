package com.bantar.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonRawValue;
import java.util.List;

@SuppressWarnings("unused")
public class EventDTO {
    private long id;
    private String name;
    private String friendlyName;
    private Object style;
    private LocalDate fromDate;
    private LocalDate untilDate;
    private List<EventQuestionDTO> questions;

    public EventDTO(long id, String name, String friendlyName, Object style,
                    LocalDate fromDate, LocalDate untilDate,
                    List<EventQuestionDTO> questions) {
        this.id = id;
        this.name = name;
        this.friendlyName = friendlyName;
        this.style = style;
        this.fromDate = fromDate;
        this.untilDate = untilDate;
        this.questions = questions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    @JsonRawValue
    public Object getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public List<EventQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<EventQuestionDTO> questions) {
        this.questions = questions;
    }
}
