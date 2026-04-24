package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Event fired after a queue modification (add, remove, move, shuffle, clear).
 * <p>
 * The {@code affectedTracks} and {@code index} values describe the items affected by the update:
 * <ul>
 *     <li>{@link QueueUpdateType#ADDED}: the added track and its queue index (affectedTracks contains one track).</li>
 *     <li>{@link QueueUpdateType#ADDED_PLAYLIST}: the added tracks and the starting queue index.</li>
 *     <li>{@link QueueUpdateType#REMOVED}: the removed track and its previous queue index (affectedTracks contains one track).</li>
 *     <li>{@link QueueUpdateType#MOVED}: the moved track and its new queue index (affectedTracks contains one track).</li>
 *     <li>{@link QueueUpdateType#SHUFFLED}: no tracks are targeted ({@code affectedTracks == null}, {@code index == -1}).</li>
 *     <li>{@link QueueUpdateType#CLEARED}: no tracks are targeted ({@code affectedTracks == null}, {@code index == -1}).</li>
 * </ul>
 *
 * @param client the client responsible for handling guild interactions
 * @param guild the guild whose queue was updated
 * @param type the type of update that occurred
 * @param affectedTracks the tracks affected by this update, or {@code null} if no tracks are targeted
 * @param index the relevant queue index for the update, or {@code -1} if no single index is targeted
 *
 * @see QueueUpdateType
 */
public record QueueUpdateEvent(
        JDA client,
        Guild guild,
        @NotNull QueueUpdateType type,
        @Nullable List<PlayableTrack> affectedTracks,
        int index
) implements MusicEvent {

    public enum QueueUpdateType {
        /**
         * A track was added to the queue.
         */
        ADDED,
        /**
         * Multiple tracks (e.g., from a playlist) were added to the queue.
         */
        ADDED_PLAYLIST,
        /**
         * A track was removed from the queue.
         */
        REMOVED,
        /**
         * A track was moved to another index in the queue.
         */
        MOVED,
        /**
         * The queue order was randomized.
         */
        SHUFFLED,
        /**
         * All tracks were removed from the queue.
         */
        CLEARED
    }
}
