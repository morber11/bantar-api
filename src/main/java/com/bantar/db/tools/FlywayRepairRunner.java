package com.bantar.db.tools;

import org.flywaydb.core.Flyway;

public class FlywayRepairRunner {
    public static void main(String[] args) {
        String url = "jdbc:h2:file:./data/bantar";
        String user = "sa";
        String password = "password";

        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .load();

        System.out.println("Repairing Flyway schema history...");
        flyway.repair();
        System.out.println("Repair complete.");
    }
}
