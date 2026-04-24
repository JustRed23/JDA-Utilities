package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlaybackState;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event fired when the playback state of a guild changes.
 * @param client the client responsible for handling guild interactions
 * @param guild the guild whose playback state changed
 * @param member the member responsible for the playback state change, if applicable, otherwise null
 * @param oldState the old state of the player
 * @param newState the new state of the player
 */
public record PlaybackStateChangeEvent(
        JDA client,
        Guild guild,
        @Nullable Member member,
        @NotNull PlaybackState oldState,
        @NotNull PlaybackState newState
) implements MusicEvent {}
