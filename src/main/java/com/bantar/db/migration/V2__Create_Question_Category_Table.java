package com.bantar.db.migration;

import com.bantar.db.DatabaseMigrationHelper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

@SuppressWarnings("unused")
public class V2__Create_Question_Category_Table extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String createTableSQL = "CREATE TABLE QUESTION_CATEGORY (" +
                "QUESTION_CATEGORY_ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "QUESTION_ID INT NOT NULL, " +
                "CATEGORY_CODE VARCHAR(100), " +
                "CONSTRAINT FK_QUESTION FOREIGN KEY (QUESTION_ID) REFERENCES QUESTION(ID), " +
                "CONSTRAINT UNIQUE_QUESTION_CATEGORY UNIQUE (QUESTION_ID, CATEGORY_CODE)" +
                ")";
        DatabaseMigrationHelper.createTableIfNotExists(context.getConnection(), "QUESTION_CATEGORY", createTableSQL);
    }
}
