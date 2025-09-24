package com.bantar.db.migration;

import com.bantar.db.DatabaseMigrationHelper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;

@SuppressWarnings("unused")
public class V4__Create_Category_Table extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        String createTableSQL = "CREATE TABLE CATEGORY (" +
                "CATEGORY_CODE VARCHAR(50) PRIMARY KEY, " +
                "DESCRIPTION VARCHAR(255) NOT NULL" +
                ")";

        DatabaseMigrationHelper.createTableIfNotExists(connection, "CATEGORY", createTableSQL);

        String insertSQL = "INSERT INTO CATEGORY (CATEGORY_CODE, DESCRIPTION) VALUES (?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {

            insertStmt.setString(1, "ICEBREAKER");
            insertStmt.setString(2, "Icebreakers");
            insertStmt.addBatch();

            insertStmt.setString(1, "CASUAL");
            insertStmt.setString(2, "Casual");
            insertStmt.addBatch();

            insertStmt.setString(1, "ROMANTIC");
            insertStmt.setString(2, "Romantic");
            insertStmt.addBatch();

            insertStmt.setString(1, "NSFW");
            insertStmt.setString(2, "Not Safe For Work/18+");
            insertStmt.addBatch();

            insertStmt.setString(1, "BUSINESS");
            insertStmt.setString(2, "Business");
            insertStmt.addBatch();

            insertStmt.setString(1, "HOBBIES");
            insertStmt.setString(2, "Hobbies");
            insertStmt.addBatch();

            insertStmt.setString(1, "SPORTS");
            insertStmt.setString(2, "Sports");
            insertStmt.addBatch();

            insertStmt.setString(1, "PHILOSOPHICAL");
            insertStmt.setString(2, "Philosophical");
            insertStmt.addBatch();

            insertStmt.setString(1, "ART");
            insertStmt.setString(2, "Art");
            insertStmt.addBatch();

            insertStmt.setString(1, "TRAVEL");
            insertStmt.setString(2, "Travel");
            insertStmt.addBatch();

            insertStmt.setString(1, "TELEVISION_MOVIES");
            insertStmt.setString(2, "Television /Movies");
            insertStmt.addBatch();

            insertStmt.setString(1, "FASHION");
            insertStmt.setString(2, "Fashion");
            insertStmt.addBatch();

            insertStmt.setString(1, "LIFESTYLE");
            insertStmt.setString(2, "Lifestyle");
            insertStmt.addBatch();

            insertStmt.setString(1, "FOOD_DRINK");
            insertStmt.setString(2, "Food & Drink");
            insertStmt.addBatch();

            insertStmt.setString(1, "FUN_HUMOUR");
            insertStmt.setString(2, "Fun / Humour");
            insertStmt.addBatch();

            insertStmt.setString(1, "SCIENCE");
            insertStmt.setString(2, "Science");
            insertStmt.addBatch();

            insertStmt.setString(1, "RELATIONSHIPS");
            insertStmt.setString(2, "Relationships");
            insertStmt.addBatch();

            insertStmt.setString(1, "CHILDHOOD");
            insertStmt.setString(2, "Childhood");
            insertStmt.addBatch();

            insertStmt.executeBatch();
        }

        String addForeignKeySQL = "ALTER TABLE QUESTION_CATEGORY " +
                "ADD CONSTRAINT FK_CATEGORY " +
                "FOREIGN KEY (CATEGORY_CODE) REFERENCES CATEGORY(CATEGORY_CODE)";
        try (PreparedStatement addFkStmt = connection.prepareStatement(addForeignKeySQL)) {
            addFkStmt.execute();
        }
    }
}
