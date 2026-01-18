package com.bantar.db.migration;

import com.bantar.db.tools.DatabaseMigrationHelper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;

@SuppressWarnings("unused")
public class V21__Insert_initial_toplists extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        DatabaseMigrationHelper.batchInsertIntoTable(
                connection,
                "TOPLIST", "ID, TEXT",
                "(1, 'Top 10 Best Movies of All Time')",
                "(2, 'Top 10 Fast Food Chains')",
                "(3, 'Top 10 Must Read Books')",
                "(4, 'Top 10 Footballers of All Time')",
                "(5, 'Top 10 Must Visit Travel Destinations')",
                "(6, 'Top 10 Comfort Foods')",
                "(7, 'Top 10 TV Shows of All Time')",
                "(8, 'Top 10 Soft Drinks')",
                "(9, 'Top 10 Weird Hobbies')",
                "(10, 'Top 10 Worst Fashion Trends')");

        DatabaseMigrationHelper.batchInsertIntoTable(
                connection,
                "TOPLIST_CATEGORY", "TOPLIST_ID, CATEGORY_CODE",
                "(1, 'TELEVISION_MOVIES')",
                "(1, 'CASUAL')",
                "(2, 'CASUAL')",
                "(2, 'FOOD_DRINK')",
                "(3, 'CASUAL')",
                "(3, 'HOBBIES')",
                "(3, 'ART')",
                "(4, 'CASUAL')",
                "(4, 'SPORTS')",
                "(5, 'CASUAL')",
                "(5, 'TRAVEL')",
                "(6, 'CASUAL')",
                "(6, 'FOOD_DRINK')",
                "(7, 'TELEVISION_MOVIES')",
                "(7, 'CASUAL')",
                "(8, 'CASUAL')",
                "(8, 'FOOD_DRINK')",
                "(9, 'CASUAL')",
                "(9, 'HOBBIES')",
                "(10, 'CASUAL')",
                "(10, 'FASHION')");

        // Reset identity sequences because of manual insertion
        DatabaseMigrationHelper.setIdentitySequence(connection, "TOPLIST", "ID");
        DatabaseMigrationHelper.setIdentitySequence(connection, "TOPLIST_CATEGORY", "TOPLIST_CATEGORY_ID");
    }
}
