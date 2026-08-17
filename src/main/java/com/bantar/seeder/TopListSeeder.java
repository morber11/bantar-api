package com.bantar.seeder;

import com.bantar.model.TopListCategory;
import org.springframework.stereotype.Component;

@Component
public class TopListSeeder implements Seeder {

    private final SeederJdbcHelper helper;

    public TopListSeeder(SeederJdbcHelper helper) {
        this.helper = helper;
    }

    @Override
    public void seed() {
        for (TopListSeedData.Item item : TopListSeedData.ITEMS) {
            long id = helper.findOrInsertTopList(item.text());
            for (TopListCategory category : item.categories()) {
                helper.insertTopListCategory(id, category.name());
            }
        }
    }
}
