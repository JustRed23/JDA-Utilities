package dev.JustRed23.jdautils.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;

public interface GuildMusicManager {

    Guild guild();
    void play(@NotNull PlayableTrack track, @NotNull AudioChannel channel);
    void pause();
    void resume();
    void stop();
    void disconnect();

    @NotNull Optional<PlayableTrack> getCurrentTrack();

    long getTrackPosition();

    GuildPlayerOptions options();

    default GuildQueueOptions queue() {
        throw new UnsupportedOperationException("Queue is not supported by this player");
    }

    default GuildPlaylistManager playlist() {
        throw new UnsupportedOperationException("Playlists are not supported by this player");
    }

    default void seek(long positionMillis) {
        throw new UnsupportedOperationException("Seeking is not supported by this player");
    }

    default void seek(@NotNull Duration position) {
        seek(position.toMillis());
    }

    default boolean togglePause() {
        if (!hasTrack()) {
            return isPaused();
        }

        if (isPaused()) resume();
        else pause();

        return isPaused();
    }

    default boolean hasTrack() {
        return getCurrentTrack().isPresent();
    }

    default @NotNull PlaybackState getPlaybackState() {
        return PlaybackState.IDLE;
    }

    default boolean isPlaying() {
        return getPlaybackState() == PlaybackState.PLAYING;
    }

    default boolean isPaused() {
        return getPlaybackState() == PlaybackState.PAUSED;
    }

    default long getRemainingTime() {
        PlayableTrack track = getCurrentTrack().orElse(null);
        if (track == null || !track.hasDuration()) {
            return -1;
        }
        long position = getTrackPosition();
        if (position < 0) {
            return -1;
        }
        return track.durationMillis() - position;
    }
}
