package com.bantar.db.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static com.bantar.config.Constants.QUESTIONS_ICEBREAKERS_WITH_CATEGORY;
import static com.bantar.db.migration.tools.DatabaseMigrationHelper.readJsonResource;

@SuppressWarnings("unused")
public class V3__Add_Initial_Questions extends BaseJavaMigration {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClassLoader classLoader = getClass().getClassLoader();

    @Override
    public void migrate(Context context) throws Exception {
        JsonNode root = readJsonResource(QUESTIONS_ICEBREAKERS_WITH_CATEGORY);
        if (root == null || !root.isArray()) {
            throw new RuntimeException("Invalid or empty JSON array in resource file.");
        }

        Connection connection = context.getConnection();
        String sql = "INSERT INTO QUESTION (ID, TEXT) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (JsonNode node : root) {
                int id = Integer.parseInt(node.get("id").asText());
                String text = node.get("text").asText();

                stmt.setInt(1, id);
                stmt.setString(2, text);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
