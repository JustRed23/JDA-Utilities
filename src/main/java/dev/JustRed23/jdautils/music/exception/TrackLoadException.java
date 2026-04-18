package dev.JustRed23.jdautils.music.exception;

/**
 * Exception thrown when a track fails to load.
 */
public class TrackLoadException extends PlayerException {

    public TrackLoadException(String message) {
        super(message);
    }

    public TrackLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrackLoadException(Throwable cause) {
        super(cause);
    }
}
