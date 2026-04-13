package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TrackErrorEvent(
        long guildId,
        @Nullable PlayableTrack track,
        @NotNull Throwable error
) implements MusicEvent {}
