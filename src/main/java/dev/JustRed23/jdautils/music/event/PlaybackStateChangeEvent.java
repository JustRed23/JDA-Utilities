package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlaybackState;
import org.jetbrains.annotations.NotNull;

public record PlaybackStateChangeEvent(
        long guildId,
        @NotNull PlaybackState oldState,
        @NotNull PlaybackState newState
) implements MusicEvent {}
