package dev.JustRed23.jdautils.settings;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Setting(long guildId, String name, Object value) {

    @NotNull
    @Contract("_, _, _ -> new")
    public static Setting of(long guildId, String name, Object value) {
        return new Setting(guildId, name, value);
    }

    public int intValue() {
        return value instanceof Integer ? (Integer) value : Integer.parseInt(stringValue());
    }

    public long longValue() {
        return value instanceof Long ? (Long) value : Long.parseLong(stringValue());
    }

    public double doubleValue() {
        return value instanceof Double ? (Double) value : Double.parseDouble(stringValue());
    }

    public boolean booleanValue() {
        return value instanceof Boolean ? (Boolean) value : ("true".equalsIgnoreCase(stringValue()) || "1".equals(stringValue()));
    }

    public String stringValue() {
        return value instanceof String ? (String) value : value.toString();
    }
}
