package com.bantar.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SuppressWarnings("unused")
public class V6__Add_Icebreaker_Category_To_All_Existing_Questions extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        String selectQuestionsSQL = "SELECT ID FROM QUESTION";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuestionsSQL);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                int questionId = rs.getInt("ID");

                String checkCategorySQL = "SELECT 1 FROM QUESTION_CATEGORY WHERE QUESTION_ID = ? AND CATEGORY_CODE = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkCategorySQL)) {
                    checkStmt.setInt(1, questionId);
                    checkStmt.setString(2, "ICEBREAKER");

                    try (ResultSet checkRs = checkStmt.executeQuery()) {
                        if (!checkRs.next()) {
                            String insertCategorySQL = "INSERT INTO QUESTION_CATEGORY (QUESTION_ID, CATEGORY_CODE) VALUES (?, ?)";
                            try (PreparedStatement insertStmt = connection.prepareStatement(insertCategorySQL)) {
                                insertStmt.setInt(1, questionId);
                                insertStmt.setString(2, "ICEBREAKER");
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }
}
