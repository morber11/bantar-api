package com.bantar.model;

import com.bantar.util.EnumUtils;

import java.util.List;

public enum DebateCategory {
    CASUAL("Casual"),
    POLICY("Policy"),
    ETHICS("Ethics"),
    TECHNOLOGY("Technology"),
    ENVIRONMENT("Environment"),
    EDUCATION("Education");

    private final String displayName;

    DebateCategory(String displayName) {
        this.displayName = displayName;
    }

    @SuppressWarnings("unused")
    public String getDisplayName() {
        return displayName;
    }

    public static DebateCategory fromString(String category) {
        return EnumUtils.fromStringIgnoreCase(DebateCategory.class, category);
    }

    public static List<DebateCategory> fromStrings(List<String> categories) {
        return EnumUtils.fromStringsIgnoreCase(DebateCategory.class, categories);
    }
}
