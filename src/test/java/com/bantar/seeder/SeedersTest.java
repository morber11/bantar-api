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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeedersTest {

    private static final List<String> PARENT_TABLES = List.of("ICEBREAKER", "DEBATE", "MIND_READER", "TOPLIST");
    private static final List<String> ALL_TABLES = List.of(
            "ICEBREAKER", "ICEBREAKER_CATEGORY", "DEBATE", "DEBATE_CATEGORY",
            "MIND_READER", "MIND_READER_CATEGORY", "TOPLIST", "TOPLIST_CATEGORY",
            "EVENT", "EVENT_QUESTIONS");

    private static JdbcTemplate jdbc;
    private static IcebreakerSeeder icebreakerSeeder;
    private static DebateSeeder debateSeeder;
    private static MindReaderSeeder mindReaderSeeder;
    private static TopListSeeder topListSeeder;
    private static EventSeeder eventSeeder;

    @BeforeAll
    static void createSchema() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:seeders;DB_CLOSE_DELAY=-1");
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

        SeederJdbcHelper helper = new SeederJdbcHelper(jdbc);
        icebreakerSeeder = new IcebreakerSeeder(helper);
        debateSeeder = new DebateSeeder(helper);
        mindReaderSeeder = new MindReaderSeeder(helper);
        topListSeeder = new TopListSeeder(helper);
        eventSeeder = new EventSeeder(helper);
    }

    @BeforeEach
    void clearTables() {
        jdbc.update("DELETE FROM EVENT_QUESTIONS");
        jdbc.update("DELETE FROM EVENT");
        jdbc.update("DELETE FROM ICEBREAKER_CATEGORY");
        jdbc.update("DELETE FROM ICEBREAKER");
        jdbc.update("DELETE FROM DEBATE_CATEGORY");
        jdbc.update("DELETE FROM DEBATE");
        jdbc.update("DELETE FROM MIND_READER_CATEGORY");
        jdbc.update("DELETE FROM MIND_READER");
        jdbc.update("DELETE FROM TOPLIST_CATEGORY");
        jdbc.update("DELETE FROM TOPLIST");
    }

    @Test
    void seedingTwiceDoesNotDuplicateAnything() {
        seedAll();
        List<Integer> firstRun = rowCounts();
        seedAll();

        assertTrue(firstRun.stream().allMatch(rows -> rows > 0));
        assertEquals(firstRun, rowCounts());
    }

    @Test
    void everyQuestionHasAtLeastOneCategory() {
        seedAll();

        assertEquals(0, count("SELECT COUNT(*) FROM ICEBREAKER WHERE ID NOT IN (SELECT QUESTION_ID FROM ICEBREAKER_CATEGORY)"));
        assertEquals(0, count("SELECT COUNT(*) FROM DEBATE WHERE ID NOT IN (SELECT DEBATE_ID FROM DEBATE_CATEGORY)"));
        assertEquals(0, count("SELECT COUNT(*) FROM MIND_READER WHERE ID NOT IN (SELECT MIND_READER_ID FROM MIND_READER_CATEGORY)"));
        assertEquals(0, count("SELECT COUNT(*) FROM TOPLIST WHERE ID NOT IN (SELECT TOPLIST_ID FROM TOPLIST_CATEGORY)"));
    }

    @Test
    void seededQuestionTextsAreUniqueWithinEachGroup() {
        seedAll();

        for (String table : PARENT_TABLES) {
            assertEquals(0, count("SELECT COUNT(*) FROM (SELECT LOWER(TRIM(TEXT)) FROM " + table
                    + " GROUP BY LOWER(TRIM(TEXT)) HAVING COUNT(*) > 1)"), "duplicate text in " + table);
        }

        assertEquals(0, count("SELECT COUNT(*) FROM (SELECT EVENT_ID, LOWER(TRIM(TEXT)) FROM EVENT_QUESTIONS "
                + "GROUP BY EVENT_ID, LOWER(TRIM(TEXT)) HAVING COUNT(*) > 1)"));
    }

    private static List<Integer> rowCounts() {
        return ALL_TABLES.stream()
                .map(table -> count("SELECT COUNT(*) FROM " + table))
                .toList();
    }

    private static void seedAll() {
        icebreakerSeeder.seed();
        debateSeeder.seed();
        mindReaderSeeder.seed();
        topListSeeder.seed();
        eventSeeder.seed();
    }

    private static int count(String sql) {
        return jdbc.queryForObject(sql, Integer.class);
    }
}
