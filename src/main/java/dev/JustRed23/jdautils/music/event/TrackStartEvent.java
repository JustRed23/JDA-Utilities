package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a track starts playing
 * @param client the client responsible for handling guild interactions
 * @param guild the guild where the track started playing
 * @param member the member responsible for starting the track
 * @param track the track that started playing
 * @see TrackEndEvent
 */
public record TrackStartEvent(
        JDA client,
        Guild guild,
        @NotNull Member member,
        @NotNull PlayableTrack track
) implements MusicEvent {}
