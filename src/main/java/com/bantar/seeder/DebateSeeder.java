package com.bantar.seeder;

import com.bantar.model.DebateCategory;
import org.springframework.stereotype.Component;

@Component
public class DebateSeeder implements Seeder {

    private final SeederJdbcHelper helper;

    public DebateSeeder(SeederJdbcHelper helper) {
        this.helper = helper;
    }

    @Override
    public void seed() {
        for (DebateSeedData.Item item : DebateSeedData.ITEMS) {
            long id = helper.findOrInsertDebate(item.text());
            for (DebateCategory category : item.categories()) {
                helper.insertDebateCategory(id, category.name());
            }
        }
    }
}
