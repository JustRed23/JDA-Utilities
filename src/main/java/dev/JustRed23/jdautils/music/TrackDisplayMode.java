package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the display mode for tracks in the music player.
 * <p>
 * This enum defines how track information is displayed to users. The available modes are:
 * <ul>
 *     <li>{@code NONE}: No track information is displayed.</li>
 *     <li>{@code CHANNEL_STATUS}: Displays the current track information in the channel's status.</li>
 *     <li>{@code HANG_STATUS}: Displays the current track information as a hang status, showing it on the bot.</li>
 * </ul>
 * <p>
 * The display mode can be set and retrieved using the {@link GuildPlayerOptions} interface.
 */
public enum TrackDisplayMode {
    NONE,
    CHANNEL_STATUS,
    HANG_STATUS;

    public static TrackDisplayMode get(@NotNull String mode) {
        return switch (mode.toLowerCase()) {
            case "channel_status" -> CHANNEL_STATUS;
            case "hang_status" -> HANG_STATUS;
            case "none" -> NONE;
            default -> throw new IllegalArgumentException("Invalid track display mode: " + mode);
        };
    }
}
