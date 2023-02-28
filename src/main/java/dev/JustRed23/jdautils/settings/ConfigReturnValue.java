package dev.JustRed23.jdautils.settings;

public enum ConfigReturnValue {
    ERROR,
    INVALID_VALUE,
    NOT_FOUND,
    SUCCESS;

    private Exception exception;

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
