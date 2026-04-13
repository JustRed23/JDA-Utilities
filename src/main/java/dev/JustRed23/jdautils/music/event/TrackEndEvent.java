package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TrackEndEvent(
        long guildId,
        @NotNull PlayableTrack track,
        @Nullable Throwable error
) implements MusicEvent {}
