package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the display mode for tracks in the music player.
 * <p>
 * This enum defines how track information is displayed to users, controlling
 * whether and where current track information is shown during playback.
 */
public enum TrackDisplayMode {
    /**
     * No track information is displayed.
     */
    NONE,

    /**
     * Displays the current track information in the channel's status.
     */
    CHANNEL_STATUS,

    /**
     * Displays the current track information as a hang status on the bot.
     */
    HANG_STATUS;

    /**
     * Parses a string identifier into a TrackDisplayMode enum value.
     * <p>
     * Accepts case-insensitive identifiers: "none", "channel_status", "hang_status".
     *
     * @param mode The string identifier to parse.
     * @return The corresponding TrackDisplayMode.
     * @throws IllegalArgumentException if the mode string is not recognized.
     */
    public static TrackDisplayMode get(@NotNull String mode) {
        return switch (mode.toLowerCase()) {
            case "channel_status" -> CHANNEL_STATUS;
            case "hang_status" -> HANG_STATUS;
            case "none" -> NONE;
            default -> throw new IllegalArgumentException("Invalid track display mode: " + mode);
        };
    }
}
