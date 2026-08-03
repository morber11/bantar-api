package com.bantar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("integration")
@SpringBootTest
class BaselineSeedingIdempotencyTest {

    @TestConfiguration
    static class DistinctContextMarker {
        @Bean
        String idempotencyProbeMarker() {
            return "idempotency-probe";
        }
    }

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void secondApplicationContextAgainstSameDatabaseChangesNothing() {
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

    private int count(String table) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    }
}
