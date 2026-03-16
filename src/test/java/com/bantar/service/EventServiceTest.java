package com.bantar.service;

import com.bantar.dto.EventDTO;
import com.bantar.dto.EventQuestionDTO;
import com.bantar.entity.AvailabilityType;
import com.bantar.entity.EventEntity;
import com.bantar.entity.EventQuestionEntity;
import com.bantar.repository.EventQuestionRepository;
import com.bantar.repository.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventQuestionRepository eventQuestionRepository;

    private Clock clock;
    private EventService eventService;
    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
        eventService = new EventService(eventRepository, eventQuestionRepository, clock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void eventsAreMappedWithQuestionsAndIds() {
        LocalDate date = LocalDate.now(clock);

        EventEntity ev = new EventEntity();
        ev.setId(1L);
        ev.setName("TEST_EVENT");
        ev.setFriendlyName("Test Event (Every Day)");
        ev.setFromDate(date.minusDays(1));
        ev.setUntilDate(date.plusDays(1));

        EventQuestionEntity q = new EventQuestionEntity();
        q.setId(42L);
        q.setText("a test event question");
        q.setEvent(ev);

        when(eventRepository.getAvailableEvents(date)).thenReturn(List.of(ev));
        when(eventRepository.getAllEvents()).thenReturn(List.of());
        when(eventQuestionRepository.findAll()).thenReturn(List.of(q));
        when(eventQuestionRepository.findByEventIdIn(List.of(ev.getId()))).thenReturn(List.of(q));

        eventService.refresh();

        List<EventDTO> events = eventService.getCurrentEvents();
        assertFalse(events.isEmpty());

        EventDTO dto = events.stream().filter(e -> "TEST_EVENT".equals(e.getName())).findFirst().orElse(null);
        assertNotNull(dto);
        assertEquals("Test Event (Every Day)", dto.getFriendlyName());
        List<EventQuestionDTO> qs = dto.getQuestions();
        assertNotNull(qs);
        assertTrue(qs.stream().anyMatch(qd -> qd.getText().contains("test event question")));
        assertTrue(qs.stream().anyMatch(qd -> qd.getId() == 42L));
    }

    @Test
    void specificDatesEventsIncludedFromAllEvents() {
        LocalDate date = LocalDate.now(clock);

        EventEntity ev = new EventEntity();
        ev.setId(2L);
        ev.setName("SPEC_EVENT");
        ev.setFriendlyName("Specific");
        ev.setType(AvailabilityType.SpecificDates);
        ev.setDatesJson("[\"" + date + "\"]");

        when(eventRepository.getAvailableEvents(date)).thenReturn(List.of());
        when(eventRepository.getAllEvents()).thenReturn(List.of(ev));
        when(eventQuestionRepository.findAll()).thenReturn(List.of());

        eventService.refresh();

        List<EventDTO> events = eventService.getCurrentEvents();
        assertTrue(events.stream().anyMatch(e -> "SPEC_EVENT".equals(e.getName())));
    }
}
