package dev.JustRed23.jdautils.component;

import java.util.ArrayList;
import java.util.List;

public class ComponentIdentifier {

    private static final List<Object> identifiers = new ArrayList<>();

    public static void checkUnique(Object identifier) {
        if (identifiers.contains(identifier))
            throw new IllegalArgumentException("Identifier is not unique");
        identifiers.add(identifier);
    }
}
