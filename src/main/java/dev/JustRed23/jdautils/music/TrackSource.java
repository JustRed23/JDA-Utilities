package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;

public enum TrackSource {
    UNKNOWN,
    YOUTUBE,
    SPOTIFY,
    CUSTOM;

    public static @NotNull TrackSource get(String identifier) {
        return switch (identifier.toLowerCase()) {
            case "youtube" -> YOUTUBE;
            case "spotify" -> SPOTIFY;
            case "custom" -> CUSTOM;
            default -> UNKNOWN;
        };
    }
}
