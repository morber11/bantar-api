package com.bantar.seeder;

import com.bantar.model.IcebreakerCategory;
import org.springframework.stereotype.Component;

@Component
public class IcebreakerSeeder implements Seeder {

    private final SeederJdbcHelper helper;

    public IcebreakerSeeder(SeederJdbcHelper helper) {
        this.helper = helper;
    }

    @Override
    public void seed() {
        for (IcebreakerSeedData.Item item : IcebreakerSeedData.ITEMS) {
            long id = helper.findOrInsertIcebreaker(item.text(), item.copyIndex());
            for (IcebreakerCategory category : item.categories()) {
                helper.insertIcebreakerCategory(id, category.name());
            }
        }
    }
}
