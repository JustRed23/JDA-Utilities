package dev.JustRed23.jdautils.settings;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class GuildSettingManager {

    private final Map<String, Object> defaults;
    private final boolean ready;

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
        try {
            initialize();
            ready = true;
        } catch (Exception e) {
            throw new RuntimeException("Could not create database", e);
        }
    }

    /**
     * Used to initialize the setting manager, this is called in the constructor
     */
    protected abstract void initialize() throws Exception;

    /**
     * Sets the given setting to the given value
     * @param guildId The id of the guild
     * @param setting The setting to set
     * @return SUCCESS if the setting was set or ERROR if an error occurred
     * @see ConfigReturnValue
     */
    public abstract ConfigReturnValue set(long guildId, @NotNull Setting setting);

    /**
     * Sets the given setting to the given value
     * @param guildId The id of the guild
     * @param setting The setting to set
     * @param value The value to set the setting to
     * @return SUCCESS if the setting was set or ERROR if an error occurred
     * @see ConfigReturnValue
     */
    public abstract ConfigReturnValue set(long guildId, @NotNull String setting, @NotNull Object value);

    /**
     * Gets the given setting, if it exists, otherwise returns the default value provided
     * @param guildId The id of the guild
     * @param setting The setting to get
     * @param defaultValue The default value to return if the setting doesn't exist
     * @return The setting, if it exists, otherwise the default value
     */
    @NotNull
    public abstract Setting getOrDefault(long guildId, @NotNull String setting, @NotNull Object defaultValue);

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
     * @return SUCCESS if the setting was deleted or NOT_FOUND if the setting doesn't exist
     * @see ConfigReturnValue
     */
    public abstract ConfigReturnValue delete(long guildId, @NotNull String setting);

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

    /**
     * Signals that the setting manager is ready to be used
     * @return True if the setting manager is ready, false otherwise
     */
    public boolean isReady() {
        return ready;
    }
}
