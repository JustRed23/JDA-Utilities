package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event fired when a track finishes playing.
 * @param client the client responsible for handling guild interactions
 * @param guild the guild where playback ended
 * @param track the track that ended, never null
 */
public record TrackEndEvent(
        JDA client,
        Guild guild,
        @NotNull PlayableTrack track,
        boolean mayStartNext
) implements MusicEvent {}
