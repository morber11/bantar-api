package com.bantar.model;

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

    public String getDisplayName() {
        return displayName;
    }

    public static QuestionCategory fromString(String category) {
        for (QuestionCategory c : QuestionCategory.values()) {
            if (c.name().equalsIgnoreCase(category)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + category);
    }
}
