package dev.JustRed23.jdautils.music.exception;

/**
 * Exception thrown when an error occurs in the music player.
 */
public class PlayerException extends RuntimeException {

    public PlayerException(String message) {
        super(message);
    }

    public PlayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerException(Throwable cause) {
        super(cause);
    }
}
