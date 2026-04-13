package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a track starts playing
 * @param guildId the id of the guild where the track started playing
 * @param track the track that started playing
 */
public record TrackStartEvent(
        long guildId,
        @NotNull PlayableTrack track
) implements MusicEvent {}
