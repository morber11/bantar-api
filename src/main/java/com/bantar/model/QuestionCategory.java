package com.bantar.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public enum QuestionCategory {
    ICEBREAKER("Icebreakers"),
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

    QuestionCategory(String displayName) {
        this.displayName = displayName;
    }

    @SuppressWarnings("unused")
    public String getDisplayName() {
        return displayName;
    }

    public static QuestionCategory fromString(String category) {
        return com.bantar.util.EnumUtils.fromStringIgnoreCase(QuestionCategory.class, category);
    }

    public static List<QuestionCategory> fromStrings(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        return categories.stream()
                .map(String::toUpperCase)
                .map(category -> {
                    try {
                        return QuestionCategory.valueOf(category);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
