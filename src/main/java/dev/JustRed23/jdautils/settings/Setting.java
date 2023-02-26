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
        return (int) value;
    }

    public long longValue() {
        return (long) value;
    }

    public double doubleValue() {
        return (double) value;
    }

    public boolean booleanValue() {
        return (boolean) value;
    }

    public String stringValue() {
        return (String) value;
    }
}
