package dev.JustRed23.jdautils.data;

import org.jetbrains.annotations.ApiStatus;

import java.lang.constant.Constable;
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

    @SuppressWarnings("unchecked")
    public <T extends Constable> T orElse(T defaultValue) {
        if (this != SUCCESS)
            return defaultValue;

        return switch (defaultValue.getClass().getSimpleName()) {
            case "String" -> (T) asString();
            case "Integer" -> (T) Integer.valueOf(value);
            case "Double" -> (T) Double.valueOf(value);
            case "Float" -> (T) Float.valueOf(value);
            case "Long" -> (T) Long.valueOf(value);
            case "Boolean" -> (T) Boolean.valueOf(value);
            default -> throw new IllegalStateException("Unsupported type " + defaultValue.getClass().getName());
        };
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
