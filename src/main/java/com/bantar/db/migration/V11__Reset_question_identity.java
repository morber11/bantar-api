package com.bantar.db.migration;

import com.bantar.db.tools.DatabaseMigrationHelper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;

@SuppressWarnings("unused")
public class V11__Reset_question_identity extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        // Advance/reset identity/auto-increment columns for tables that may have been
        // populated with explicit IDs so later generated-key inserts won't collide.
        DatabaseMigrationHelper.setIdentitySequence(connection, "QUESTION", "ID");
        DatabaseMigrationHelper.setIdentitySequence(connection, "DEBATE", "ID");
        DatabaseMigrationHelper.setIdentitySequence(connection, "AI_QUESTION", "ID");
        DatabaseMigrationHelper.setIdentitySequence(connection, "QUESTION_CATEGORY", "QUESTION_CATEGORY_ID");
        DatabaseMigrationHelper.setIdentitySequence(connection, "DEBATE_CATEGORY", "DEBATE_CATEGORY_ID");
    }
}
