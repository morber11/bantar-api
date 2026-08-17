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

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaselineSeedersTest {

    private static JdbcTemplate jdbc;
    private static IcebreakerSeeder icebreakerSeeder;
    private static DebateSeeder debateSeeder;
    private static MindReaderSeeder mindReaderSeeder;
    private static TopListSeeder topListSeeder;
    private static EventSeeder eventSeeder;

    @BeforeAll
    static void createSchema() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:baselineSeeders;DB_CLOSE_DELAY=-1");
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
    void seedMatchesBaselineSnapshotAndIsIdempotent() {
        seedAll();
        assertBaselineCounts();
        seedAll();
        assertBaselineCounts();
    }

    @Test
    void correctedIcebreakerTextAndV19Question() {
        seedAll();

        assertEquals(1, count("SELECT COUNT(*) FROM ICEBREAKER " +
                "WHERE TEXT = 'Would you rather spend a day at the beach or poolside?'"));
        assertEquals(0, count("SELECT COUNT(*) FROM ICEBREAKER " +
                "WHERE TEXT LIKE 'Would you rather spend a day at the beach or poolside? 41.%'"));
        assertEquals(1, count("SELECT COUNT(*) FROM ICEBREAKER " +
                "WHERE TEXT = 'What''s your favorite thing about your current job?'"));
        assertEquals(1, count("SELECT COUNT(*) FROM ICEBREAKER_CATEGORY ic " +
                "JOIN ICEBREAKER i ON ic.QUESTION_ID = i.ID " +
                "WHERE i.TEXT = 'What''s your favorite thing about your current job?' " +
                "AND ic.CATEGORY_CODE = 'BUSINESS'"));
    }

    @Test
    void duplicateTextsProduceSeparateRowsWithTheirOwnCategories() {
        seedAll();

        assertEquals(2, count("SELECT COUNT(*) FROM ICEBREAKER " +
                "WHERE TEXT = 'What''s your favorite family tradition?'"));
        assertEquals(5, count("SELECT COUNT(*) FROM ICEBREAKER_CATEGORY ic " +
                "JOIN ICEBREAKER i ON ic.QUESTION_ID = i.ID " +
                "WHERE i.TEXT = 'What''s your favorite family tradition?'"));
        assertEquals(1, count("SELECT COUNT(*) FROM ICEBREAKER_CATEGORY ic " +
                "JOIN ICEBREAKER i ON ic.QUESTION_ID = i.ID " +
                "WHERE i.TEXT = 'What''s your favorite family tradition?' AND ic.CATEGORY_CODE = 'LIFESTYLE'"));
    }

    @Test
    void multiCategoryQuestionsRetainEveryCategory() {
        seedAll();

        assertEquals(3, count("SELECT COUNT(*) FROM ICEBREAKER_CATEGORY ic " +
                "JOIN ICEBREAKER i ON ic.QUESTION_ID = i.ID " +
                "WHERE i.TEXT = 'Would you rather spend a day at the beach or poolside?'"));
        assertEquals(2, count("SELECT COUNT(*) FROM ICEBREAKER_CATEGORY ic " +
                "JOIN ICEBREAKER i ON ic.QUESTION_ID = i.ID " +
                "WHERE i.TEXT = 'What''s a sport that everyone should try once?'"));
        assertEquals(3, count("SELECT COUNT(*) FROM MIND_READER_CATEGORY c " +
                "JOIN MIND_READER m ON c.MIND_READER_ID = m.ID " +
                "WHERE m.TEXT = 'What colour are my eyes?'"));
        assertEquals(3, count("SELECT COUNT(*) FROM TOPLIST_CATEGORY c " +
                "JOIN TOPLIST t ON c.TOPLIST_ID = t.ID " +
                "WHERE t.TEXT = 'Top 10 Must Read Books'"));
    }

    @Test
    void eventSeededWithAllQuestionsAndFields() {
        seedAll();

        assertEquals(1, count("SELECT COUNT(*) FROM EVENT WHERE NAME = 'ST_PATRICKS_DAY' " +
                "AND FRIENDLY_NAME = 'St Patrick''s Day' AND TYPE = 0 " +
                "AND FROM_DATE = DATE '2026-03-16' AND UNTIL_DATE = DATE '2026-03-20' " +
                "AND IS_DELETED = FALSE"));
        assertEquals(40, count("SELECT COUNT(*) FROM EVENT_QUESTIONS"));
        assertEquals(0, count("SELECT COUNT(*) FROM EVENT_QUESTIONS " +
                "WHERE EVENT_ID NOT IN (SELECT ID FROM EVENT)"));
        assertEquals(1, count("SELECT COUNT(*) FROM EVENT_QUESTIONS " +
                "WHERE TEXT = 'Have you ever been Irish Dancing (c\u00e9il\u00ed)?'"));
    }

    private static void seedAll() {
        icebreakerSeeder.seed();
        debateSeeder.seed();
        mindReaderSeeder.seed();
        topListSeeder.seed();
        eventSeeder.seed();
    }

    private static void assertBaselineCounts() {
        assertEquals(1200, count("SELECT COUNT(*) FROM ICEBREAKER"));
        assertEquals(2215, count("SELECT COUNT(*) FROM ICEBREAKER_CATEGORY"));
        assertEquals(192, count("SELECT COUNT(*) FROM DEBATE"));
        assertEquals(199, count("SELECT COUNT(*) FROM DEBATE_CATEGORY"));
        assertEquals(46, count("SELECT COUNT(*) FROM MIND_READER"));
        assertEquals(122, count("SELECT COUNT(*) FROM MIND_READER_CATEGORY"));
        assertEquals(10, count("SELECT COUNT(*) FROM TOPLIST"));
        assertEquals(21, count("SELECT COUNT(*) FROM TOPLIST_CATEGORY"));
        assertEquals(1, count("SELECT COUNT(*) FROM EVENT"));
        assertEquals(40, count("SELECT COUNT(*) FROM EVENT_QUESTIONS"));
    }

    private static int count(String sql) {
        return jdbc.queryForObject(sql, Integer.class);
    }
}
