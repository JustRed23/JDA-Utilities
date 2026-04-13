package dev.JustRed23.jdautils.music.exception;

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
