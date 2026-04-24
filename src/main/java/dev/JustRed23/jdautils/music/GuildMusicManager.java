package dev.JustRed23.jdautils.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;

/**
 * Manages audio playback and queue management for a specific guild.
 * <p>
 * Provides a unified API for controlling playback (play, pause, resume, stop), managing playback position,
 * and accessing queue and playlist features. Implementations may support various features such as queue management,
 * playlists, and seeking. Default method implementations provide basic functionality for common operations.
 */
public interface GuildMusicManager {

    /**
     * @return The guild associated with this music manager.
     */
    Guild guild();

    /**
     * Plays a track from the specified URL in the given audio channel.
     *
     * @param url The URL of the track to play.
     * @param channel The audio channel in which to play the track.
     * @param member The member who requested the track.
     */
    void play(@NotNull String url, @NotNull AudioChannel channel, @NotNull Member member);

    /**
     * Pauses the currently playing track.
     */
    void pause();

    /**
     * Resumes the currently paused track from where it was paused.
     */
    void resume();

    /**
     * Stops the currently playing track.
     */
    void stop();

    /**
     * Joins the specified audio channel without starting playback. This can be used to prepare the music manager for playback in a specific channel.
     */
    void join(@NotNull AudioChannel channel);

    /**
     * Binds the music manager to the specified text channel for sending playback-related messages. This does not affect audio playback and is optional.
     */
    void bind(@NotNull TextChannel channel);

    /**
     * Stops playback and disconnects from the audio channel.
     */
    void disconnect();

    /**
     * Gets the audio channel that the music manager is currently connected to.
     *
     * @return The current audio channel, or null if not connected to any channel.
     */
    @Nullable AudioChannel getCurrentChannel();

    /**
     * Gets the text channel that the music manager is currently bound to for sending playback-related messages.
     *
     * @return The bound text channel, or null if not bound to any channel.
     */
    @Nullable TextChannel getBoundChannel();

    /**
     * Destroys the music manager and releases all resources. The music manager should not be used after calling this method.
     */
    void destroy();

    /**
     * Convenience method for playing a track using a PlayableTrack object.
     *
     * @param track The track to play.
     * @param channel The audio channel in which to play the track.
     */
    default void play(@NotNull PlayableTrack track, @NotNull AudioChannel channel, @NotNull Member member) {
        play(track.url(), channel, member);
    }

    /**
     * Gets the currently playing track.
     *
     * @return An Optional containing the currently playing track, or empty if no track is playing.
     */
    @NotNull Optional<PlayableTrack> getCurrentTrack();

    /**
     * Gets the current position of the playing track.
     *
     * @return The current playback position in milliseconds, or -1 if no track is playing.
     */
    long getTrackPosition();

    /**
     * Gets the player options for this music manager.
     *
     * @return The guild player options.
     */
    GuildPlayerOptions options();

    /**
     * Gets the queue options for this music manager.
     *
     * @return The guild queue options.
     * @throws UnsupportedOperationException if queue management is not supported.
     */
    default GuildQueueOptions queue() {
        throw new UnsupportedOperationException("Queue is not supported by this player");
    }

    /**
     * Gets the playlist manager for this music manager.
     *
     * @return The guild playlist manager.
     * @throws UnsupportedOperationException if playlist management is not supported.
     */
    default GuildPlaylistManager playlist() {
        throw new UnsupportedOperationException("Playlists are not supported by this player");
    }

    /**
     * Seeks to the specified position in the currently playing track.
     *
     * @param positionMillis The position to seek to in milliseconds.
     * @throws UnsupportedOperationException if seeking is not supported.
     */
    default void seek(long positionMillis) {
        throw new UnsupportedOperationException("Seeking is not supported by this player");
    }

    /**
     * Seeks to the specified position in the currently playing track.
     *
     * @param position The position to seek to.
     * @throws UnsupportedOperationException if seeking is not supported.
     */
    default void seek(@NotNull Duration position) {
        seek(position.toMillis());
    }

    /**
     * Toggles the pause state of the currently playing track.
     *
     * @return true if the track is now paused, false if now playing or no track is playing.
     */
    default boolean togglePause() {
        if (!hasTrack()) return false;

        if (isPaused()) resume();
        else pause();

        return isPaused();
    }

    /**
     * Checks if the music manager is currently connected to an audio channel.
     *
     * @return true if connected to an audio channel, false otherwise.
     */
    default boolean isConnected() {
        return getCurrentChannel() != null;
    }

    /**
     * Checks if a track is currently being played.
     *
     * @return true if a track is playing, false otherwise.
     */
    default boolean hasTrack() {
        return getCurrentTrack().isPresent();
    }

    /**
     * Gets the current playback state.
     *
     * @return The current playback state.
     * @see PlaybackState
     */
    default @NotNull PlaybackState getPlaybackState() {
        return PlaybackState.IDLE;
    }

    /**
     * Checks if a track is currently playing.
     *
     * @return true if the playback state is {@link PlaybackState#PLAYING}, false otherwise.
     */
    default boolean isPlaying() {
        return getPlaybackState() == PlaybackState.PLAYING;
    }

    /**
     * Checks if a track is currently paused.
     *
     * @return true if the playback state is {@link PlaybackState#PAUSED}, false otherwise.
     */
    default boolean isPaused() {
        return getPlaybackState() == PlaybackState.PAUSED;
    }

    /**
     * Gets the remaining time of the currently playing track.
     *
     * @return The remaining time in milliseconds, 0 if a stream is playing, or -1 if no track is playing or the track has no duration.
     */
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
