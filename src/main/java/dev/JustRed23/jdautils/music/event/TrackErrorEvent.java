package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event published when playback fails due to an exception.
 * <p>
 * Unlike {@link TrackEndEvent}, this event is error-only and always contains a non-null cause.
 * The {@code track} may be {@code null} when failure happens before a concrete track is available
 * (for example, load/prepare failures).
 *
 * @param guildId the id of the guild where the failure occurred
 * @param track the track associated with the failure, or {@code null} if failure occurred before one was available
 * @param error the exception that caused the failure
 *
 * @see TrackEndEvent
 */
public record TrackErrorEvent(
        long guildId,
        @Nullable PlayableTrack track,
        @NotNull Throwable error
) implements MusicEvent {}
