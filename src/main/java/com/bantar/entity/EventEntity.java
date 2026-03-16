package com.bantar.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "EVENT")
public class EventEntity {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "NAME", length = 500)
    private String name;

    @Column(name = "FRIENDLY_NAME", length = 500)
    private String friendlyName;

    @Column(name = "STYLE", columnDefinition = "text")
    private String style;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "TYPE")
    private AvailabilityType type;


    @Column(name = "FROM_DATE")
    private LocalDate fromDate;

    @Column(name = "UNTIL_DATE")
    private LocalDate untilDate;

    @Column(name = "DATES_JSON", columnDefinition = "text")
    private String datesJson;

    @Column(name = "DAYS_OF_WEEK_JSON", columnDefinition = "text")
    private String daysOfWeekJson;

    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted = false;

    public EventEntity() {
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public void setType(AvailabilityType type) {
        this.type = type;
    }


    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public void setUntilDate(LocalDate untilDate) {
        this.untilDate = untilDate;
    }

    public void setDatesJson(String datesJson) {
        this.datesJson = datesJson;
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

    public String getStyle() {
        return style;
    }

    public AvailabilityType getType() {
        return type;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getUntilDate() {
        return untilDate;
    }

    public List<LocalDate> getDates() {
        if (datesJson == null || datesJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            List<String> raw = MAPPER.readValue(datesJson, new TypeReference<>() {
            });
            return raw.stream().map(LocalDate::parse).toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<String> getDaysOfWeek() {
        if (daysOfWeekJson == null || daysOfWeekJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(daysOfWeekJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
