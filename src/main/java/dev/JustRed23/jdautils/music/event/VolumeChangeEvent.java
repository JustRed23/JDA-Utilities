package dev.JustRed23.jdautils.music.event;

/**
 * Event fired when the volume of the music player changes
 * @param guildId the id of the guild where the volume was changed
 * @param oldVolume the old volume
 * @param newVolume the new volume
 */
public record VolumeChangeEvent(
        long guildId,
        float oldVolume,
        float newVolume
) implements MusicEvent {}
