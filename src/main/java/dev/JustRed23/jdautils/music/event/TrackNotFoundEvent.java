package dev.JustRed23.jdautils.music.event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a track was not found
 * @param client the client responsible for handling guild interactions
 * @param guild thg guild where the track was not found
 * @param url the url that was attempted to be loaded
 */
public record TrackNotFoundEvent(
        JDA client,
        Guild guild,
        @NotNull String url
) implements MusicEvent {}
