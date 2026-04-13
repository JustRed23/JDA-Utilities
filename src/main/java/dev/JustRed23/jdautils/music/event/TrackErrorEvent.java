package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event published when playback fails due to an exception.
 * <p>
 * Unlike {@link TrackEndEvent}, this event is error-only and always contains a non-null cause.
 * The {@code track} may be {@code null} when failure happens before a concrete track is available
 * (for example, load/prepare failures).
 *
 * @param client the client responsible for handling guild interactions
 * @param guild the guild where the failure occurred
 * @param track the track associated with the failure, or {@code null} if failure occurred before one was available
 * @param error the exception that caused the failure
 *
 * @see TrackEndEvent
 */
public record TrackErrorEvent(
        JDA client,
        Guild guild,
        @Nullable PlayableTrack track,
        @NotNull Throwable error
) implements MusicEvent {}
