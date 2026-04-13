package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlaybackState;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when the playback state of a guild changes.
 * @param guildId the id of the guild whose playback state changed
 * @param oldState the old state of the player
 * @param newState the new state of the player
 */
public record PlaybackStateChangeEvent(
        long guildId,
        @NotNull PlaybackState oldState,
        @NotNull PlaybackState newState
) implements MusicEvent {}
