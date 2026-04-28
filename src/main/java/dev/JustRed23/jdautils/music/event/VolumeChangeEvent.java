package dev.JustRed23.jdautils.music.event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Event fired when the volume of the music player changes
 * @param client the client responsible for handling guild interactions
 * @param guild the guild where the volume was changed
 * @param oldVolume the old volume (0-100)
 * @param newVolume the new volume (0-100)
 */
public record VolumeChangeEvent(
        JDA client,
        Guild guild,
        int oldVolume,
        int newVolume
) implements MusicEvent {}
