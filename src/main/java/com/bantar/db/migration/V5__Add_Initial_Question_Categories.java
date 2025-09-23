package com.bantar.db.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static com.bantar.config.Constants.QUESTIONS_ICEBREAKERS_WITH_CATEGORY;
import static com.bantar.db.DatabaseMigrationHelper.readJsonResource;

@SuppressWarnings("unused")
public class V5__Add_Initial_Question_Categories extends BaseJavaMigration {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClassLoader classLoader = getClass().getClassLoader();

    @Override
    public void migrate(Context context) throws Exception {
        JsonNode root = readJsonResource(QUESTIONS_ICEBREAKERS_WITH_CATEGORY);
        if (root == null || !root.isArray()) {
            throw new RuntimeException("Invalid or empty JSON array in resource file.");
        }

        Connection connection = context.getConnection();

        String sql = "INSERT INTO QUESTION_CATEGORY (QUESTION_ID, CATEGORY_CODE) VALUES (?, ?)";

        try (PreparedStatement categoryStmt = connection.prepareStatement(sql)) {

            for (JsonNode node : root) {
                int questionId = Integer.parseInt(node.get("id").asText());

                JsonNode categoriesNode = node.get("categories");
                if (categoriesNode != null && categoriesNode.isArray()) {
                    for (JsonNode categoryNode : categoriesNode) {
                        String category = categoryNode.asText();

                        categoryStmt.setInt(1, questionId);
                        categoryStmt.setString(2, category);
                        categoryStmt.addBatch();
                    }
                    categoryStmt.executeBatch();
                }
            }
        }
    }
}
