package dev.JustRed23.jdautils.music.event;

public record VolumeChangeEvent(
        long guildId,
        float oldVolume,
        float newVolume
) implements MusicEvent {}
