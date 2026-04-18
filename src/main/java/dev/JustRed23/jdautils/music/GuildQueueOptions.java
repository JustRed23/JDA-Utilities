package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Manages the playback queue for a guild.
 */
public interface GuildQueueOptions {

    /**
     * Skips to the next track in the queue.
     *
     * @return true if a next track was available, false otherwise.
     */
    boolean skip();

    /**
     * Goes back to the previous track in history.
     *
     * @return true if a previous track was available, false otherwise.
     */
    boolean back();

    /**
     * Clears all tracks from the queue.
     */
    void clear();

    /**
     * Shuffles the queue.
     */
    void shuffle();

    /**
     * Gets the current queue of tracks.
     *
     * @return A list of tracks in the queue.
     */
    @NotNull List<PlayableTrack> getQueue();

    /**
     * Gets the history of previously played tracks.
     *
     * @return A list of tracks in the history.
     */
    @NotNull List<PlayableTrack> getHistory();

    /**
     * Peeks at the next track without removing it.
     *
     * @return The next track, or null if the queue is empty.
     */
    default @Nullable PlayableTrack peek() {
        List<PlayableTrack> queue = getQueue();
        return queue.isEmpty() ? null : queue.get(0);
    }
}
