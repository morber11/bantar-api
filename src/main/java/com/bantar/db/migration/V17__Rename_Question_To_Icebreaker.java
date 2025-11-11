package com.bantar.db.migration;

import com.bantar.db.tools.DatabaseMigrationHelper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("unused")
public class V17__Rename_Question_To_Icebreaker extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            try {
                stmt.execute("DELETE FROM QUESTION_CATEGORY WHERE UPPER(CATEGORY_CODE) = 'ICEBREAKER'");
            } catch (Exception ignored) {
            }

            try {
                stmt.execute("ALTER TABLE QUESTION RENAME TO ICEBREAKER");
            } catch (Exception ignore) {
            }

            try {
                stmt.execute("ALTER TABLE QUESTION_CATEGORY RENAME TO ICEBREAKER_CATEGORY");
            } catch (Exception ignore) {
            }

            // old table
            try {
                stmt.execute("DROP TABLE IF EXISTS CATEGORY CASCADE");
            } catch (Exception e) {
                try {
                    String q = "SELECT CONSTRAINT_NAME, TABLE_NAME FROM INFORMATION_SCHEMA.CONSTRAINTS WHERE REFERENCED_TABLE_NAME = 'CATEGORY'";
                    try (ResultSet rs = stmt.executeQuery(q)) {
                        while (rs.next()) {
                            String constraint = rs.getString(1);
                            String table = rs.getString(2);
                            try {
                                stmt.execute(String.format("ALTER TABLE %s DROP CONSTRAINT %s", table, constraint));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                } catch (Exception ignored) {
                }

                try {
                    stmt.execute("ALTER TABLE ICEBREAKER_CATEGORY DROP CONSTRAINT FK_CATEGORY");
                } catch (Exception ignored) {
                }
                try {
                    stmt.execute("ALTER TABLE QUESTION_CATEGORY DROP CONSTRAINT FK_CATEGORY");
                } catch (Exception ignored) {
                }

                try {
                    stmt.execute("DROP TABLE IF EXISTS CATEGORY");
                } catch (Exception ignored) {
                }
            }
        }

        try {
            DatabaseMigrationHelper.setIdentitySequence(context.getConnection(), "ICEBREAKER", "ID");
        } catch (Exception ignore) {
        }

        try {
            DatabaseMigrationHelper.setIdentitySequence(context.getConnection(), "ICEBREAKER_CATEGORY", "QUESTION_CATEGORY_ID");
        } catch (Exception ignore) {
        }
    }
}
