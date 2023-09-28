package dev.JustRed23.jdautils.settings;

import org.jetbrains.annotations.ApiStatus;

public enum ConfigReturnValue {
    /**
     * Signals that an SQL related error occurred, see {@link #getException()} for more information
     */
    ERROR,
    /**
     * Signals that the provided value was outside the scope / not valid
     */
    INVALID_VALUE,
    /**
     * Signals that the requested setting was not found
     */
    NOT_FOUND,
    /**
     * Signals that the current settings manager was not ready to handle the request
     */
    NOT_READY,
    SUCCESS;

    private Exception exception;

    @ApiStatus.Internal
    public ConfigReturnValue setException(Exception exception) {
        if (this != ERROR)
            throw new IllegalStateException("Cannot set exception when return value is not ERROR");
        this.exception = exception;
        return this;
    }

    /**
     * Gets the exception that was thrown when the return value is ERROR
     * @return The exception that was thrown
     */
    public Exception getException() {
        return exception;
    }
}
