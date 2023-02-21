package dev.JustRed23.jdautils.settings;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class GuildSettingManager {

    private final Map<String, Object> defaults;

    protected GuildSettingManager() {
        this(Collections.emptyMap());
    }

    /**
     * Creates a new GuildSettingManager with the given default settings
     * <br>These settings will be added to the database when a guild is added
     * @param globalDefaults The default settings for all guilds
     */
    protected GuildSettingManager(@NotNull Map<String, Object> globalDefaults) {
        this.defaults = globalDefaults;
    }

    /**
     * Sets the given setting to the given value
     * @param guildId The id of the guild
     * @param setting The setting to set
     */
    public abstract void set(long guildId, @NotNull Setting setting);

    /**
     * Sets the given setting to the given value
     * @param guildId The id of the guild
     * @param setting The setting to set
     * @param value The value to set the setting to
     */
    public abstract void set(long guildId, @NotNull String setting, @NotNull Object value);

    /**
     * Gets the given setting, if it exists
     * @param guildId The id of the guild
     * @param setting The setting to get
     * @return The setting, if it exists
     * @see Optional
     * @see #has(long, String)
     */
    @NotNull
    public abstract Optional<Setting> get(long guildId, @NotNull String setting);

    /**
     * Checks if the given setting exists
     * @param guildId The id of the guild
     * @param setting The setting to check
     * @return True if the setting exists, false otherwise
     */
    public abstract boolean has(long guildId, @NotNull String setting);

    /**
     * Deletes the given setting
     * @param guildId The id of the guild
     * @param setting The setting to delete
     * @return True if the setting was deleted, false otherwise
     */
    public abstract boolean delete(long guildId, @NotNull String setting);

    /**
     * Gets all the settings for the given guild
     * @param guildId The id of the guild
     * @return The settings for the given guild
     */
    @NotNull
    public abstract List<Setting> getSettings(long guildId);

    /**
     * Creates the table for the given guild
     * @param guildId The id of the guild
     */
    protected abstract void create(long guildId);

    /**
     * Clears and removes the table for the given guild
     * @param guildId The id of the guild
     */
    protected abstract void clear(long guildId);

    /**
     * Shuts down the setting manager, this is useful for databases
     */
    public void shutdown() {}

    /**
     * Adds the given guild to the setting manager
     * @param guildId The id of the guild
     */
    public final void addGuild(long guildId) {
        create(guildId);

        for (Map.Entry<String, Object> entry : defaults.entrySet())
            if (!has(guildId, entry.getKey()))
                set(guildId, entry.getKey(), entry.getValue());
    }

    /**
     * Adds the given guilds to the setting manager
     * @param guilds The guilds to add
     */
    public final void loadGuilds(@NotNull List<Guild> guilds) {
        for (Guild guild : guilds)
            addGuild(guild.getIdLong());
    }

    /**
     * Removes the given guild from the setting manager
     * @param guildId The id of the guild
     */
    public final void removeGuild(long guildId) {
        for (Setting setting : getSettings(guildId))
            delete(guildId, setting.name());

        clear(guildId);
    }
}
