package dev.JustRed23.jdautils.music.event;

import org.jetbrains.annotations.NotNull;

/**
 * Listener interface for receiving music events.
 * <p>
 * Implement this interface and register it with the event bus to receive music event callbacks.
 * All methods are optional and can be overridden as needed.
 * @see EventBus
 */
public interface MusicEventListener {

    /**
     * Called when a track starts playing.
     *
     * @param event The track start event.
     */
    default void onTrackStart(@NotNull TrackStartEvent event) {}

    /**
     * Called when a track finishes or is stopped.
     *
     * @param event The track end event.
     */
    default void onTrackEnd(@NotNull TrackEndEvent event) {}

    /**
     * Called when the playback state changes.
     *
     * @param event The playback state change event.
     */
    default void onPlaybackStateChange(@NotNull PlaybackStateChangeEvent event) {}

    /**
     * Called when a track fails to load or play.
     *
     * @param event The track error event.
     */
    default void onTrackError(@NotNull TrackErrorEvent event) {}

    /**
     * Called when a track search yields no results.
     *
     * @param event The track not found event.
     */
    default void onTrackNotFound(@NotNull TrackNotFoundEvent event) {}

    /**
     * Called when the queue is updated.
     *
     * @param event The queue update event.
     */
    default void onQueueUpdate(@NotNull QueueUpdateEvent event) {}

    /**
     * Called when the volume changes.
     *
     * @param event The volume change event.
     */
    default void onVolumeChange(@NotNull VolumeChangeEvent event) {}

    /**
     * Called for custom events or events without specific handlers.
     *
     * @param event The custom event.
     */
    default void onCustomEvent(@NotNull MusicEvent event) {}
}
