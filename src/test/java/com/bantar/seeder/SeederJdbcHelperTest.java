package com.bantar.seeder;

import com.bantar.db.migration.V1__Create_Initial_Schema;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.migration.Context;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeederJdbcHelperTest {

    private static JdbcTemplate jdbc;
    private static SeederJdbcHelper helper;

    @BeforeAll
    static void createSchema() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:seederHelper;DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(ds);
        Context context = new Context() {
            @Override
            public Connection getConnection() {
                try {
                    return jdbc.getDataSource().getConnection();
                } catch (java.sql.SQLException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public Configuration getConfiguration() {
                return null;
            }
        };
        new V1__Create_Initial_Schema().migrate(context);
        helper = new SeederJdbcHelper(jdbc);
    }

    @BeforeEach
    void clearTables() {
        String[] tables = {"EVENT_QUESTIONS", "EVENT", "ICEBREAKER_CATEGORY", "ICEBREAKER",
                "DEBATE_CATEGORY", "DEBATE", "MIND_READER_CATEGORY", "MIND_READER",
                "TOPLIST_CATEGORY", "TOPLIST"};
        for (String table : tables) {
            jdbc.update("DELETE FROM " + table);
        }
    }

    @Test
    void firstInsertInsertsAndReturnsGeneratedId() {
        long id = helper.findOrInsertIcebreaker("What is your favorite color?", 1);

        assertTrue(id > 0);
        assertEquals(1, count("ICEBREAKER"));
        assertEquals(1, jdbc.queryForObject(
                "SELECT COUNT(*) FROM ICEBREAKER WHERE TEXT = ?", Integer.class, "What is your favorite color?"));
    }

    @Test
    void repeatExecutionReusesExistingId() {
        long first = helper.findOrInsertIcebreaker("What is your dream job?", 1);
        long second = helper.findOrInsertIcebreaker("What is your dream job?", 1);

        assertEquals(first, second);
        assertEquals(1, count("ICEBREAKER"));
    }

    @Test
    void generatedIdsAreDistinctAcrossParents() {
        long a = helper.findOrInsertDebate("Debate alpha?");
        long b = helper.findOrInsertDebate("Debate beta?");

        assertNotEquals(a, b);
        assertEquals(2, count("DEBATE"));
    }

    @Test
    void duplicateCategoryRelationshipIsPrevented() {
        long id = helper.findOrInsertIcebreaker("Which trait do you value most?", 1);

        helper.insertIcebreakerCategory(id, "CASUAL");
        helper.insertIcebreakerCategory(id, "CASUAL");

        assertEquals(1, count("ICEBREAKER_CATEGORY"));
    }

    @Test
    void duplicateTextCopiesAreTrackedByCopyIndex() {
        long first = helper.findOrInsertIcebreaker("What was your favorite subject in school?", 1);
        long second = helper.findOrInsertIcebreaker("What was your favorite subject in school?", 2);

        assertNotEquals(first, second);
        assertEquals(2, count("ICEBREAKER"));
        assertEquals(first, helper.findOrInsertIcebreaker("What was your favorite subject in school?", 1));
        assertEquals(second, helper.findOrInsertIcebreaker("What was your favorite subject in school?", 2));
        assertEquals(2, count("ICEBREAKER"));
    }

    @Test
    void duplicateEventQuestionIsPrevented() {
        long eventId = helper.findOrInsertEvent("ST_PATRICKS_DAY", "St Patrick's Day", 0,
                LocalDate.of(2026, 3, 16), LocalDate.of(2026, 3, 20), null);

        helper.insertEventQuestion(eventId, "What's the craic?");
        helper.insertEventQuestion(eventId, "What's the craic?");

        assertEquals(1, count("EVENT_QUESTIONS"));
        assertEquals(1, count("EVENT"));
    }

    private static int count(String table) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    }
}
