package com.bantar.db.migration;

import com.bantar.db.migration.tools.DatabaseMigrationHelper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

@SuppressWarnings("unused")
public class V9__Create_Debate_Category_Table extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String createTableSQL = "CREATE TABLE DEBATE_CATEGORY (" +
                "DEBATE_CATEGORY_ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "DEBATE_ID INT NOT NULL, " +
                "CATEGORY_CODE VARCHAR(100), " +
                "CONSTRAINT FK_DEBATE FOREIGN KEY (DEBATE_ID) REFERENCES DEBATE(ID), " +
                "CONSTRAINT UNIQUE_DEBATE_CATEGORY UNIQUE (DEBATE_ID, CATEGORY_CODE)" +
                ")";
        DatabaseMigrationHelper.createTableIfNotExists(context.getConnection(), "DEBATE_CATEGORY", createTableSQL);
    }
}
