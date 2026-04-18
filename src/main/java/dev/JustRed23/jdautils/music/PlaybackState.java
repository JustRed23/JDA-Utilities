package dev.JustRed23.jdautils.music;

/**
 * Represents the current state of music playback.
 * <p>
 * This enum defines the possible states that a music player can be in during its lifecycle.
 */
public enum PlaybackState {
    /**
     * The player is idle with no track loaded or playing.
     */
    IDLE,

    /**
     * A track is currently being loaded or prepared for playback.
     */
    LOADING,

    /**
     * A track is actively playing.
     */
    PLAYING,

    /**
     * Playback is paused but a track is loaded and ready to resume.
     */
    PAUSED,

    /**
     * An error occurred during playback or track loading.
     */
    ERROR
}
