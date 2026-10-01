package com.bantar.service;

import com.bantar.dto.EventDTO;
import com.bantar.dto.EventQuestionDTO;
import com.bantar.entity.EventEntity;
import com.bantar.repository.EventRepository;
import com.bantar.repository.EventQuestionRepository;
import com.bantar.entity.EventQuestionEntity;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.context.annotation.DependsOn;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@DependsOn("baselineDataSeeder")
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final EventQuestionRepository eventQuestionRepository;
    private final Clock clock;

    private final AtomicReference<Map<Long, List<EventQuestionDTO>>> cachedQuestionsByEvent = new AtomicReference<>();

    @Autowired
    public EventService(EventRepository eventRepository, EventQuestionRepository eventQuestionRepository, Clock clock) {
        this.eventRepository = eventRepository;
        this.eventQuestionRepository = eventQuestionRepository;
        this.clock = clock;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        try {
            loadQuestions();
        } catch (Exception e) {
            logger.error("Failed to initialize EventService", e);
            throw new IllegalStateException("EventService initialization failed", e);
        }
    }

    public List<EventDTO> getCurrentEvents() {
        LocalDate date = LocalDate.now(clock);

        ensureQuestionsLoaded();
        Map<Long, List<EventQuestionDTO>> questionsByEvent = cachedQuestionsByEvent.get();

        List<EventEntity> rangeEvents = eventRepository.getAvailableEvents(date);
        List<EventEntity> allEvents = eventRepository.getAllEvents();
        DayOfWeek dow = date.getDayOfWeek();

        List<EventEntity> extra = allEvents.stream()
                .filter(e -> e.getType() != null)
                .filter(e -> switch (e.getType()) {
                    case SpecificDates -> e.getDates() != null && e.getDates().contains(date);
                    case Weekly -> e.getDaysOfWeek() != null && e.getDaysOfWeek().stream()
                            .anyMatch(d -> d.equalsIgnoreCase(dow.name()));
                    default -> false;
                })
                .toList();

        // combine and deduplicate by id
        List<EventEntity> combined = new ArrayList<>(rangeEvents.stream()
                .collect(Collectors.toMap(EventEntity::getId, e -> e, (a, b) -> a))
                .values());

        for (EventEntity e : extra) {
            boolean exists = combined.stream().anyMatch(x -> x.getId() == e.getId());
            if (!exists)
                combined.add(e);
        }

        return combined.stream()
                .map(ev -> {
                    List<EventQuestionDTO> qs = questionsByEvent.getOrDefault(ev.getId(), Collections.emptyList());
                    List<EventQuestionDTO> qdto = qs.stream()
                            .map(q -> new EventQuestionDTO(q.getId(), q.getText()))
                            .collect(Collectors.toList());

                    return new EventDTO(ev.getId(), ev.getName(), ev.getFriendlyName(), ev.getStyle(), ev.getFromDate(),
                            ev.getUntilDate(), qdto);
                })
                .collect(Collectors.toList());
    }

    public void refresh() {
        loadQuestions();
    }

    private synchronized void loadQuestions() {
        List<EventQuestionEntity> questions = eventQuestionRepository.findAll();
        Map<Long, List<EventQuestionDTO>> map = new HashMap<>();
        for (EventQuestionEntity q : questions) {
            if (q == null || q.getEvent() == null)
                continue;
            long eventId = q.getEvent().getId();
            map.computeIfAbsent(eventId, k -> new ArrayList<>()).add(new EventQuestionDTO(q.getId(), q.getText()));
        }
        cachedQuestionsByEvent.set(map);
    }

    private void ensureQuestionsLoaded() {
        if (cachedQuestionsByEvent.get() == null) {
            synchronized (this) {
                if (cachedQuestionsByEvent.get() == null)
                    loadQuestions();
            }
        }
    }
}
