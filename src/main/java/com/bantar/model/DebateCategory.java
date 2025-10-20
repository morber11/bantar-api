package com.bantar.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public enum DebateCategory {
    DEBATE("Debate"),
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
        return com.bantar.util.EnumUtils.fromStringIgnoreCase(DebateCategory.class, category);
    }

    public static List<DebateCategory> fromStrings(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        return categories.stream()
                .map(String::toUpperCase)
                .map(category -> {
                    try {
                        return DebateCategory.valueOf(category);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
