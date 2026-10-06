package com.bantar;

import com.bantar.dto.EventDTO;
import com.bantar.service.DebateService;
import com.bantar.service.EventService;
import com.bantar.service.IcebreakerService;
import com.bantar.service.MindReaderService;
import com.bantar.service.TopListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("integration")
@SpringBootTest
class SeededDataIntegrationTest {

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2026-03-17T12:00:00Z"), ZoneId.of("UTC"));
        }
    }

    @Autowired
    private IcebreakerService icebreakerService;

    @Autowired
    private DebateService debateService;

    @Autowired
    private MindReaderService mindReaderService;

    @Autowired
    private TopListService topListService;

    @Autowired
    private EventService eventService;

    @Test
    void servicesSeeSeededDataAtFirstUse() {
        assertFalse(icebreakerService.getAll().isEmpty());
        assertFalse(debateService.getAll().isEmpty());
        assertFalse(mindReaderService.getAll().isEmpty());
        assertFalse(topListService.getAll().isEmpty());
    }

    @Test
    void currentEventIsReturnedWithItsQuestions() {
        List<EventDTO> events = eventService.getCurrentEvents();

        EventDTO stPatricks = events.stream()
                .filter(e -> "ST_PATRICKS_DAY".equals(e.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(stPatricks);
        assertFalse(stPatricks.getQuestions().isEmpty());
    }
}
