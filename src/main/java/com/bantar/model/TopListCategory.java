package com.bantar.model;

import com.bantar.util.EnumUtils;

import java.util.List;

public enum TopListCategory {
    CASUAL("Casual"),
    HOBBIES("Hobbies"),
    SPORTS("Sports"),
    TRAVEL("Travel"),
    TELEVISION_MOVIES("Television / Movies"),
    FOOD_DRINK("Food & Drink"),
    ART("Art"),
    FASHION("Fashion");
    private final String displayName;

    TopListCategory(String displayName) {
        this.displayName = displayName;
    }

    @SuppressWarnings("unused")
    public String getDisplayName() {
        return displayName;
    }

    @SuppressWarnings("unused")
    public static TopListCategory fromString(String category) {
        return EnumUtils.fromStringIgnoreCase(TopListCategory.class, category);
    }

    public static List<TopListCategory> fromStrings(List<String> categories) {
        return EnumUtils.fromStringsIgnoreCase(TopListCategory.class, categories);
    }
}
