package com.bantar.db.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

public class DatabaseMigrationHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private static final Logger logger = LogManager.getLogger(DatabaseMigrationHelper.class);

    public static void createTableIfNotExists(Connection connection, String tableName, String createTableSQL)
            throws Exception {
        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName.toUpperCase(), null)) {
            if (!rs.next()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(createTableSQL);
                }
            }
        }
    }

    public static JsonNode readJsonResource(String resourcePath) {
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("Resource not found: " + resourcePath);
                return null;
            }
            return objectMapper.readTree(inputStream);
        } catch (IOException e) {
            System.err.println("Failed to read JSON from resource: " + resourcePath);
            logger.error("an error occurred", e);
            return null;
        }
    }

    public static void insertIntoTable(Connection connection, String tableName, String columns, Object... values)
            throws SQLException {

        String[] columnArray = columns.split(",");

        if (columnArray.length != values.length) {
            throw new IllegalArgumentException("Number of columns and values must match.");
        }

        String placeholders = String.join(",", Collections.nCopies(values.length, "?"));
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]); // JDBC is 1-indexed
            }
            pstmt.executeUpdate();
            logger.info("Inserted data into table: {}", tableName);
        }
    }

    // helper for batch inserts instead of having to instantiate new Object[] every
    // time
    public static Object[] row(Object... values) {
        return values;
    }

    // keeping for backwards compatibility with existing migrations
    // otherwise deprecated - DO NOT USE
    @Deprecated
    public static void batchInsertIntoTable(Connection connection, String tableName, String columns,
            String... valueRows) throws Exception {
        for (String valueRow : valueRows) {
            String sql = String.format("INSERT INTO %s (%s) VALUES %s", tableName, columns, valueRow);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
                logger.info("Inserted data into table: {}", tableName);
            }
        }
    }

    public static void batchInsertIntoTable(Connection connection, String tableName, String columns,
            Object[]... rows) throws Exception {

        String[] columnArray = columns.split(",");
        String placeholders = String.join(",", Collections.nCopies(columnArray.length, "?"));
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Object[] row : rows) {
                for (int i = 0; i < row.length; i++) {
                    pstmt.setObject(i + 1, row[i]);
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            logger.info("Batch inserted {} rows into table: {}", rows.length, tableName);
        }
    }

    public static long insertIntoTableWithIdentity(Connection connection, String tableName, String columns,
            Object... values) throws Exception {

        String[] columnArray = columns.split(",");
        String placeholders = String.join(",", Collections.nCopies(columnArray.length, "?"));
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    logger.info("Inserted data into table: {} (generated id={})", tableName, id);
                    return id;
                }
            }
        }

        logger.info("Inserted data into table: {} (no generated key)", tableName);
        return -1;
    }

    public static void setIdentitySequence(Connection connection, String tableName, String idColumn) {
        try (Statement s = connection.createStatement();
                ResultSet rs = s.executeQuery("SELECT MAX(" + idColumn + ") FROM " + tableName)) {
            if (rs.next()) {
                int maxId = rs.getInt(1);
                int restart = Math.max(1, maxId + 1);
                s.execute("ALTER TABLE " + tableName + " ALTER COLUMN " + idColumn + " RESTART WITH " + restart);
                logger.info("Set identity for {}.{} to start at {}", tableName, idColumn, restart);
            }
        } catch (Exception e) {
            logger.debug("Could not set identity sequence for {}.{}: {}", tableName, idColumn, e.getMessage());
        }
    }
}
