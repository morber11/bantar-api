package com.bantar.seeder;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Component
public class SeederJdbcHelper {

    private static final String ICEBREAKER = "ICEBREAKER";
    private static final String ICEBREAKER_CATEGORY = "ICEBREAKER_CATEGORY";
    private static final String DEBATE = "DEBATE";
    private static final String DEBATE_CATEGORY = "DEBATE_CATEGORY";
    private static final String MIND_READER = "MIND_READER";
    private static final String MIND_READER_CATEGORY = "MIND_READER_CATEGORY";
    private static final String TOPLIST = "TOPLIST";
    private static final String TOPLIST_CATEGORY = "TOPLIST_CATEGORY";
    private static final String EVENT = "EVENT";
    private static final String EVENT_QUESTIONS = "EVENT_QUESTIONS";

    private final JdbcTemplate jdbc;

    public SeederJdbcHelper(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    public long findOrInsertIcebreaker(String text, int copyIndex) {
        List<Long> ids = jdbc.query("SELECT ID FROM " + ICEBREAKER + " WHERE TEXT = ? ORDER BY ID ASC",
                (rs, rowNum) -> rs.getLong(1), text);
        if (ids.size() >= copyIndex) {
            return ids.get(copyIndex - 1);
        }
        return insertParent(ICEBREAKER, "TEXT", text);
    }

    public long findOrInsertDebate(String text) {
        return findOrInsertByColumn(DEBATE, "TEXT", text);
    }

    public long findOrInsertMindReader(String text) {
        return findOrInsertByColumn(MIND_READER, "TEXT", text);
    }

    public long findOrInsertTopList(String text) {
        return findOrInsertByColumn(TOPLIST, "TEXT", text);
    }

    public long findOrInsertEvent(String name, String friendlyName, int type, LocalDate fromDate,
                                  LocalDate untilDate, String style) {
        Long id = jdbc.query("SELECT ID FROM " + EVENT + " WHERE NAME = ?",
                rs -> rs.next() ? rs.getLong(1) : null, name);
        if (id != null) {
            return id;
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO " + EVENT + " (NAME, FRIENDLY_NAME, TYPE, FROM_DATE, UNTIL_DATE, STYLE) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, friendlyName);
            ps.setInt(3, type);
            ps.setDate(4, fromDate == null ? null : java.sql.Date.valueOf(fromDate));
            ps.setDate(5, untilDate == null ? null : java.sql.Date.valueOf(untilDate));
            ps.setString(6, style);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void insertIcebreakerCategory(long questionId, String categoryCode) {
        insertCategoryIfAbsent(ICEBREAKER_CATEGORY, "QUESTION_ID", questionId, categoryCode);
    }

    public void insertDebateCategory(long debateId, String categoryCode) {
        insertCategoryIfAbsent(DEBATE_CATEGORY, "DEBATE_ID", debateId, categoryCode);
    }

    public void insertMindReaderCategory(long mindReaderId, String categoryCode) {
        insertCategoryIfAbsent(MIND_READER_CATEGORY, "MIND_READER_ID", mindReaderId, categoryCode);
    }

    public void insertTopListCategory(long topListId, String categoryCode) {
        insertCategoryIfAbsent(TOPLIST_CATEGORY, "TOPLIST_ID", topListId, categoryCode);
    }

    public void insertEventQuestion(long eventId, String text) {
        Long id = jdbc.query("SELECT ID FROM " + EVENT_QUESTIONS + " WHERE EVENT_ID = ? AND TEXT = ?",
                rs -> rs.next() ? rs.getLong(1) : null, eventId, text);
        if (id == null) {
            jdbc.update("INSERT INTO " + EVENT_QUESTIONS + " (TEXT, EVENT_ID) VALUES (?, ?)", text, eventId);
        }
    }

    private long findOrInsertByColumn(String table, String column, String value) {
        Long id = jdbc.query("SELECT ID FROM " + table + " WHERE " + column + " = ?",
                rs -> rs.next() ? rs.getLong(1) : null, value);
        return id != null ? id : insertParent(table, column, value);
    }

    private long insertParent(String table, String column, String value) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO " + table + " (" + column + ") VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, value);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private void insertCategoryIfAbsent(String table, String parentColumn, long parentId, String categoryCode) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE " + parentColumn + " = ? AND CATEGORY_CODE = ?",
                Integer.class, parentId, categoryCode);
        if (count == 0) {
            jdbc.update("INSERT INTO " + table + " (" + parentColumn + ", CATEGORY_CODE) VALUES (?, ?)",
                    parentId, categoryCode);
        }
    }
}
