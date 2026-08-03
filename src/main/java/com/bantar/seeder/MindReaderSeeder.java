package com.bantar.seeder;

import com.bantar.model.MindReaderCategory;
import org.springframework.stereotype.Component;

@Component
public class MindReaderSeeder implements Seeder {

    private final SeederJdbcHelper helper;

    public MindReaderSeeder(SeederJdbcHelper helper) {
        this.helper = helper;
    }

    @Override
    public void seed() {
        for (MindReaderSeedData.Item item : MindReaderSeedData.ITEMS) {
            long id = helper.findOrInsertMindReader(item.text());
            for (MindReaderCategory category : item.categories()) {
                helper.insertMindReaderCategory(id, category.name());
            }
        }
    }
}
