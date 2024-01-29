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
        return Integer.parseInt(stringValue());
    }

    public long longValue() {
        return Long.parseLong(stringValue());
    }

    public double doubleValue() {
        return Double.parseDouble(stringValue());
    }

    public boolean booleanValue() {
        return Boolean.parseBoolean(stringValue());
    }

    public String stringValue() {
        return (String) value;
    }
}
