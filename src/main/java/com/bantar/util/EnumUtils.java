package com.bantar.util;

import java.util.List;

public final class EnumUtils {
    private EnumUtils() {
    }

    public static <T extends Enum<T>> T fromStringIgnoreCase(Class<T> enumClass, String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null enum name");
        }
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(name))
                return constant;
        }
        throw new IllegalArgumentException("Unknown " + enumClass.getSimpleName() + ": " + name);
    }

    public static <T extends Enum<T>> java.util.List<T> fromStringsIgnoreCase(Class<T> enumClass, List<String> names) {
        if (names == null || names.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return names.stream()
                .map(s -> {
                    try {
                        return fromStringIgnoreCase(enumClass, s);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }
}
