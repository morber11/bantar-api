package com.bantar.util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static <T extends Enum<T>> List<T> fromStringsIgnoreCase(Class<T> enumClass, List<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList();
        }
        return names.stream()
                .map(s -> {
                    try {
                        return fromStringIgnoreCase(enumClass, s);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
