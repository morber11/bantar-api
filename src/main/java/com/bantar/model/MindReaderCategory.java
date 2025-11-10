package com.bantar.model;

import com.bantar.util.EnumUtils;

import java.util.List;

public enum MindReaderCategory {
    CASUAL("Casual"),
    HOBBIES("Hobbies"),
    FUN("Fun"),
    PHYSICAL("Physical"),
    SPORTS("Sports"),
    GENERAL("General"),
    INSIGHT("Insight"),
    PERSONALITY("Personality");

    private final String displayName;

    MindReaderCategory(String displayName) {
        this.displayName = displayName;
    }

    @SuppressWarnings("unused")
    public String getDisplayName() {
        return displayName;
    }

    @SuppressWarnings("unused")
    public static MindReaderCategory fromString(String category) {
        return EnumUtils.fromStringIgnoreCase(MindReaderCategory.class, category);
    }

    public static List<MindReaderCategory> fromStrings(List<String> categories) {
        return EnumUtils.fromStringsIgnoreCase(MindReaderCategory.class, categories);
    }
}
