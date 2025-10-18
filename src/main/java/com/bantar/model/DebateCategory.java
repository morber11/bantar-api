package com.bantar.model;

public enum DebateCategory {
    DEBATE("Debate"),
    POLICY("Policy"),
    ETHICS("Ethics"),
    TECHNOLOGY("Technology"),
    ENVIRONMENT("Environment"),
    EDUCATION("Education");

    private final String displayName;

    DebateCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DebateCategory fromString(String category) {
        return com.bantar.util.EnumUtils.fromStringIgnoreCase(DebateCategory.class, category);
    }
}
