package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the source platform from which a track originates.
 * <p>
 * This enum identifies where a music track was sourced from, which can affect
 * how the track is loaded, displayed, and handled by the music system.
 */
public enum TrackSource {
    /**
     * The track source is unknown or not specified.
     */
    UNKNOWN,

    /**
     * The track originates from YouTube.
     */
    YOUTUBE,

    /**
     * The track originates from Spotify.
     */
    SPOTIFY,

    /**
     * The track originates from a custom or user-defined source.
     */
    CUSTOM;

    /**
     * Parses a string identifier into a TrackSource enum value.
     * <p>
     * Accepts case-insensitive identifiers: "youtube", "spotify", "custom".
     * Any unrecognized identifier returns {@link #UNKNOWN}.
     *
     * @param identifier The string identifier to parse.
     * @return The corresponding TrackSource, or {@link #UNKNOWN} if not recognized.
     */
    public static @NotNull TrackSource get(String identifier) {
        return switch (identifier.toLowerCase()) {
            case "youtube" -> YOUTUBE;
            case "spotify" -> SPOTIFY;
            case "custom" -> CUSTOM;
            default -> UNKNOWN;
        };
    }
}
