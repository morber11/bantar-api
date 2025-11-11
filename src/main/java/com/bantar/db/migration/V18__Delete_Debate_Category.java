package com.bantar.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

@SuppressWarnings("unused")
public class V18__Delete_Debate_Category extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            try {
                stmt.execute("DELETE FROM DEBATE_CATEGORY WHERE UPPER(CATEGORY_CODE) = 'DEBATE'");
            } catch (Exception ignored) {
            }
        }
    }
}
