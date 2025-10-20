package com.bantar.util;

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
}
