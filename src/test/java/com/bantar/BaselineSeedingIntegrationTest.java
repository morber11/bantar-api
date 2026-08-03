package com.bantar;

import com.bantar.dto.EventDTO;
import com.bantar.service.DebateService;
import com.bantar.service.EventService;
import com.bantar.service.IcebreakerService;
import com.bantar.service.MindReaderService;
import com.bantar.service.TopListService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("integration")
@SpringBootTest
class BaselineSeedingIntegrationTest {

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2026-03-17T12:00:00Z"), ZoneId.of("UTC"));
        }
    }

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbc;

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
    void flywayAppliedOnlySchema() {
        org.flywaydb.core.api.MigrationInfo[] applied = flyway.info().applied();
        assertEquals(1, applied.length);
        assertEquals("1", applied[0].getVersion().getVersion());
        assertEquals(0, jdbc.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN ('QUESTION', 'QUESTION_CATEGORY', 'CATEGORY')",
                Integer.class));
    }

    @Test
    void baselineContentSeededForAllFiveGroups() {
        assertEquals(1200, count("ICEBREAKER"));
        assertEquals(2215, count("ICEBREAKER_CATEGORY"));
        assertEquals(192, count("DEBATE"));
        assertEquals(199, count("DEBATE_CATEGORY"));
        assertEquals(46, count("MIND_READER"));
        assertEquals(122, count("MIND_READER_CATEGORY"));
        assertEquals(10, count("TOPLIST"));
        assertEquals(21, count("TOPLIST_CATEGORY"));
        assertEquals(1, count("EVENT"));
        assertEquals(40, count("EVENT_QUESTIONS"));
    }

    @Test
    void servicesSeeBaselineDataAtFirstUse() {
        assertEquals(1200, icebreakerService.getAll().size());
        assertEquals(192, debateService.getAll().size());
        assertEquals(46, mindReaderService.getAll().size());
        assertEquals(10, topListService.getAll().size());
    }

    @Test
    void eventServiceInitialCacheReturnsSeededEventQuestions() {
        List<EventDTO> events = eventService.getCurrentEvents();

        EventDTO stPatricks = events.stream()
                .filter(e -> "ST_PATRICKS_DAY".equals(e.getName()))
                .findFirst()
                .orElse(null);
        assertFalse(events.isEmpty());
        assertNotNull(stPatricks);
        assertEquals("St Patrick's Day", stPatricks.getFriendlyName());
        assertEquals(40, stPatricks.getQuestions().size());
        assertTrue(stPatricks.getQuestions().stream()
                .anyMatch(q -> "What's the craic?".equals(q.getText())));
    }

    private int count(String table) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    }
}
