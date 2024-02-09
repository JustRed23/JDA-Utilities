package dev.JustRed23.jdautils.data;

import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public enum InteractionResult {

    SUCCESS,
    DUPLICATE,
    NOT_FOUND,
    NO_CHANGE,
    ERROR;

    Throwable error;
    String value;

    @ApiStatus.Internal
    InteractionResult setError(Throwable error) {
        if (this == ERROR)
            this.error = error;
        else throw new IllegalStateException("Cannot set error for non-error result");

        return this;
    }

    @ApiStatus.Internal
    InteractionResult setValue(String value) {
        if (this == SUCCESS)
            this.value = value;
        else throw new IllegalStateException("Cannot set value for non-success result");

        return this;
    }

    public Throwable getError() {
        return error;
    }

    public String asString() {
        return value;
    }

    public int asInt() {
        return Integer.parseInt(value);
    }

    public double asDouble() {
        return Double.parseDouble(value);
    }

    public float asFloat() {
        return Float.parseFloat(value);
    }

    public long asLong() {
        return Long.parseLong(value);
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(value) || Objects.equals(value, "1");
    }

    public String orElse(String defaultValue) {
        if (this == SUCCESS)
            return value == null ? defaultValue : value;
        else return defaultValue;
    }

    public String orElseThrow() {
        if (this == SUCCESS)
            return value;
        else if (this == ERROR)
            throw new IllegalStateException("An error occurred", error);
        else
            throw new IllegalStateException("Value not found");
    }
}
