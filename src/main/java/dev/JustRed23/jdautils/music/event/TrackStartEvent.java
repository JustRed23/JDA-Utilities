package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import org.jetbrains.annotations.NotNull;

public record TrackStartEvent(
        long guildId,
        @NotNull PlayableTrack track
) implements MusicEvent {}
