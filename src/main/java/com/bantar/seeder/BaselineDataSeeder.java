package com.bantar.seeder;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Component
public class BaselineDataSeeder {

    private final List<Seeder> seeders;
    private final TransactionTemplate transactionTemplate;

    public BaselineDataSeeder(List<Seeder> seeders, PlatformTransactionManager transactionManager) {
        this.seeders = seeders;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @PostConstruct
    void seed() {
        // no transaction is active during @PostConstruct, so wrap the whole seeding run explicitly
        transactionTemplate.executeWithoutResult(status -> {
            for (Seeder seeder : seeders) {
                seeder.seed();
            }
        });
    }
}
