package dev.JustRed23.jdautils.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Unique {

    private static final Map<String, List<Object>> identifiers = new HashMap<>();

    /**
     * Checks if the object is unique
     * @param identifier A collection identifier to check if all objects in this collection are unique
     * @param object The object to check
     * @param errorMessage The error message to throw if the object is not unique
     */
    public static void checkUnique(String identifier, Object object, String errorMessage) {
        if (identifiers.containsKey(identifier)) {
            if (identifiers.get(identifier).contains(object))
                throw new IllegalArgumentException(errorMessage);
            identifiers.get(identifier).add(object);
        } else {
            identifiers.computeIfAbsent(identifier, k -> new ArrayList<>()).add(object);
        }
    }
}
