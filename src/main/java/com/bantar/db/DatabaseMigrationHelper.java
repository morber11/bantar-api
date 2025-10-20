package com.bantar.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseMigrationHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private static final Logger logger = LogManager.getLogger(DatabaseMigrationHelper.class);

    public static void createTableIfNotExists(Connection connection, String tableName, String createTableSQL) throws Exception {
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

    public static void insertIntoTable(Connection connection, String tableName, String columns, String values) throws Exception {
        String sql = String.format("INSERT INTO %s (%s) VALUES %s", tableName, columns, values);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            logger.info("Inserted data into table: {}", tableName);
        }
    }

    public static void batchInsertIntoTable(Connection connection, String tableName, String columns, String... valueRows) throws Exception {
        String values = String.join(", ", valueRows);
        insertIntoTable(connection, tableName, columns, values);
    }
}
