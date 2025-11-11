package com.bantar.model;

import com.bantar.util.EnumUtils;

import java.util.List;

public enum IcebreakerCategory {
    CASUAL("Casual"),
    ROMANTIC("Romantic"),
    NSFW("Not Safe For Work/18+"),
    BUSINESS("Business"),
    HOBBIES("Hobbies"),
    SPORTS("Sports"),
    PHILOSOPHICAL("Philosophical"),
    ART("Art"),
    TRAVEL("Travel"),
    TELEVISION_MOVIES("Television /Movies"),
    FASHION("Fashion"),
    LIFESTYLE("Lifestyle"),
    FOOD_DRINK("Food & Drink"),
    FUN_HUMOUR("Fun / Humour"),
    SCIENCE("Science"),
    RELATIONSHIPS("Relationships"),
    CHILDHOOD("Childhood");


    private final String displayName;

    IcebreakerCategory(String displayName) {
        this.displayName = displayName;
    }

    @SuppressWarnings("unused")
    public String getDisplayName() {
        return displayName;
    }

    public static IcebreakerCategory fromString(String category) {
        return EnumUtils.fromStringIgnoreCase(IcebreakerCategory.class, category);
    }

    public static List<IcebreakerCategory> fromStrings(List<String> categories) {
        return EnumUtils.fromStringsIgnoreCase(IcebreakerCategory.class, categories);
    }
}
