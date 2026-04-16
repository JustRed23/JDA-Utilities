package dev.JustRed23.jdautils.music.event;

import org.jetbrains.annotations.NotNull;

/**
 * Listener interface for music events. Implement this interface and register it with the MusicManager to receive music event callbacks.
 */
public interface MusicEventListener {

    /**
     * Called when a track starts playing
     */
    default void onTrackStart(@NotNull TrackStartEvent event) {}

    /**
     * Called when a track finishes or is stopped
     */
    default void onTrackEnd(@NotNull TrackEndEvent event) {}

    /**
     * Called when the playback state changes
     */
    default void onPlaybackStateChange(@NotNull PlaybackStateChangeEvent event) {}

    /**
     * Called when a track fails to load or play
     */
    default void onTrackError(@NotNull TrackErrorEvent event) {}

    /**
     * Called when a track search yields no results
     */
    default void onTrackNotFound(@NotNull TrackNotFoundEvent event) {}

    /**
     * Called when the queue is updated
     */
    default void onQueueUpdate(@NotNull QueueUpdateEvent event) {}

    /**
     * Called when the volume changes
     */
    default void onVolumeChange(@NotNull VolumeChangeEvent event) {}

    /**
     * Custom implementation specific events
     */
    default void onCustomEvent(@NotNull MusicEvent event) {}
}
