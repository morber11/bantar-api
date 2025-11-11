package com.bantar.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("unused")
public class V19__Fix_Icebreaker_720 extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            try {
                String selectText = "SELECT TEXT FROM ICEBREAKER WHERE ID = 720";
                String current = null;
                try (ResultSet rs = stmt.executeQuery(selectText)) {
                    if (rs.next()) {
                        current = rs.getString(1);
                    }
                }

                String broken = "Would you rather spend a day at the beach or poolside? 41. What's your favorite thing about your current job?";
                if (current != null && current.equals(broken)) {
                    try {
                        stmt.execute("UPDATE ICEBREAKER SET TEXT = 'Would you rather spend a day at the beach or poolside?' WHERE ID = 720");
                    } catch (Exception ignored) {
                    }

                    try {
                        String wanted = "What's your favorite thing about your current job?";
                        String wantedEsc = wanted.replace("'", "''");
                        String select = "SELECT ID FROM ICEBREAKER WHERE TEXT = '" + wantedEsc + "'";
                        int qid = -1;
                        try (ResultSet rs = stmt.executeQuery(select)) {
                            if (rs.next()) {
                                qid = rs.getInt(1);
                            }
                        }

                        if (qid == -1) {
                            try {
                                stmt.execute("INSERT INTO ICEBREAKER (TEXT) VALUES ('" + wantedEsc + "')");
                            } catch (Exception ignored) {
                            }

                            try (ResultSet rs = stmt.executeQuery(select)) {
                                if (rs.next()) {
                                    qid = rs.getInt(1);
                                }
                            }
                        }

                        if (qid != -1) {
                            try {
                                String check = "SELECT COUNT(*) FROM ICEBREAKER_CATEGORY WHERE QUESTION_ID = " + qid + " AND UPPER(CATEGORY_CODE) = 'BUSINESS'";
                                try (ResultSet rs = stmt.executeQuery(check)) {
                                    if (rs.next()) {
                                        int count = rs.getInt(1);
                                        if (count == 0) {
                                            try {
                                                stmt.execute("INSERT INTO ICEBREAKER_CATEGORY (QUESTION_ID, CATEGORY_CODE) VALUES (" + qid + ", 'BUSINESS')");
                                            } catch (Exception ignored) {
                                            }
                                        }
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
