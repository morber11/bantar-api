package com.bantar.seeder;

import com.bantar.model.TopListCategory;
import java.util.List;

final class TopListSeedData {

    record Item(String text, List<TopListCategory> categories) {
    }

    static final List<Item> ITEMS = List.of(
            new Item("Top 10 Best Movies of All Time", List.of(TopListCategory.CASUAL, TopListCategory.TELEVISION_MOVIES)),
            new Item("Top 10 Fast Food Chains", List.of(TopListCategory.CASUAL, TopListCategory.FOOD_DRINK)),
            new Item("Top 10 Must Read Books", List.of(TopListCategory.ART, TopListCategory.CASUAL, TopListCategory.HOBBIES)),
            new Item("Top 10 Footballers of All Time", List.of(TopListCategory.CASUAL, TopListCategory.SPORTS)),
            new Item("Top 10 Must Visit Travel Destinations", List.of(TopListCategory.CASUAL, TopListCategory.TRAVEL)),
            new Item("Top 10 Comfort Foods", List.of(TopListCategory.CASUAL, TopListCategory.FOOD_DRINK)),
            new Item("Top 10 TV Shows of All Time", List.of(TopListCategory.CASUAL, TopListCategory.TELEVISION_MOVIES)),
            new Item("Top 10 Soft Drinks", List.of(TopListCategory.CASUAL, TopListCategory.FOOD_DRINK)),
            new Item("Top 10 Weird Hobbies", List.of(TopListCategory.CASUAL, TopListCategory.HOBBIES)),
            new Item("Top 10 Worst Fashion Trends", List.of(TopListCategory.CASUAL, TopListCategory.FASHION))
    );
}
