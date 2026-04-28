package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Manages player-specific options and settings.
 */
public interface GuildPlayerOptions {

    /**
     * Sets the playback volume.
     *
     * @param volume The volume level.
     */
    void setVolume(@Range(from = 0, to = 100) int volume);

    /**
     * Gets the current playback volume.
     *
     * @return The current volume level.
     */
    @Range(from = 0, to = 100) int getVolume();

    /**
     * Sets whether the player should automatically disconnect when idle.
     *
     * @param autoDisconnect true to enable auto-disconnect, false otherwise.
     */
    void setAutoDisconnect(boolean autoDisconnect);

    /**
     * Checks if auto-disconnect is enabled.
     *
     * @return true if auto-disconnect is enabled, false otherwise.
     */
    boolean isAutoDisconnect();

    /**
     * Sets the track display mode.
     * <p>
     * Controls how track information is displayed:
     * <ul>
     *     <li>{@code NONE}: No track information is displayed</li>
     *     <li>{@code CHANNEL_STATUS}: Displays current track in the channel's status</li>
     *     <li>{@code HANG_STATUS}: Displays current track as a hang status on the bot</li>
     * </ul>
     *
     * @param displayMode The display mode to use.
     */
    void setTrackDisplayMode(@NotNull TrackDisplayMode displayMode);

    /**
     * Gets the current track display mode.
     * <p>
     * Returns one of:
     * <ul>
     *     <li>{@code NONE}: No track information is displayed</li>
     *     <li>{@code CHANNEL_STATUS}: Displays current track in the channel's status</li>
     *     <li>{@code HANG_STATUS}: Displays current track as a hang status on the bot</li>
     * </ul>
     *
     * @return The current display mode.
     */
    @NotNull TrackDisplayMode getTrackDisplayMode();

    /**
     * Sets the repeat mode for playback.
     * <p>
     * Controls how tracks are repeated:
     * <ul>
     *     <li>{@code OFF}: No repeating - play each track once</li>
     *     <li>{@code ONE}: Repeat the current track indefinitely</li>
     *     <li>{@code ALL}: Repeat the entire queue when finished</li>
     * </ul>
     *
     * @param repeatMode The repeat mode to use.
     * @throws UnsupportedOperationException if repeat mode is not supported.
     */
    default void setRepeatMode(@NotNull RepeatMode repeatMode) {
        throw new UnsupportedOperationException("Repeat mode is not supported by this player");
    }

    /**
     * Gets the current repeat mode.
     * <p>
     * Returns one of:
     * <ul>
     *     <li>{@code OFF}: No repeating - play each track once</li>
     *     <li>{@code ONE}: Repeat the current track indefinitely</li>
     *     <li>{@code ALL}: Repeat the entire queue when finished</li>
     * </ul>
     *
     * @return The current repeat mode.
     * @throws UnsupportedOperationException if repeat mode is not supported.
     */
    default @NotNull RepeatMode getRepeatMode() {
        throw new UnsupportedOperationException("Repeat mode is not supported by this player");
    }
}
