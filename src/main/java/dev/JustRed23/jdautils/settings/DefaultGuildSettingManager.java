package dev.JustRed23.jdautils.settings;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static dev.JustRed23.jdautils.settings.ConfigReturnValue.*;

/**
 * This is the default implementation of the {@link GuildSettingManager} interface.<br>
 * It uses a SQLite database to store the settings. The database is created in the same directory as the jar file and is called "JDAU-guild_settings.db".
 */
public final class DefaultGuildSettingManager extends GuildSettingManager {

    private HikariDataSource dataSource;

    public DefaultGuildSettingManager() {
        this(Collections.emptyMap());
    }

    public DefaultGuildSettingManager(Map<String, Object> globalDefaults) {
        super(globalDefaults);
        try {
            createDB();
        } catch (IOException e) {
            throw new RuntimeException("Could not create database", e);
        }
    }

    private void createDB() throws IOException {
        String dbName = "JDAU-guild_settings.db";
        File db = new File(dbName);
        if (!db.exists())
            if (!db.createNewFile())
                throw new IOException("Could not create database file");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbName);
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    //DO NOT FORGET TO CLOSE THE CONNECTION AFTER USING THIS METHOD
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public ConfigReturnValue set(long guildId, @NotNull Setting setting) {
        return set(guildId, setting.name(), setting.value());
    }

    public ConfigReturnValue set(long guildId, @NotNull String setting, @NotNull Object value) {
        if (has(guildId, setting)) {
            try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE guild_" + guildId + "_settings SET value = ? WHERE setting = ?")) {
                statement.setObject(1, value);
                statement.setString(2, setting);
                statement.execute();
            } catch (SQLException e) {
                return ERROR.setException(e);
            }
            return SUCCESS;
        }

        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement("INSERT INTO guild_" + guildId + "_settings (setting, value) VALUES (?, ?)")) {
            statement.setString(1, setting);
            statement.setObject(2, value);
            statement.execute();
        } catch (SQLException e) {
            return ERROR.setException(e);
        }
        return SUCCESS;
    }

    public @NotNull Setting getOrDefault(long guildId, @NotNull String setting, @NotNull Object defaultValue) {
        return get(guildId, setting).orElse(Setting.of(guildId, setting, defaultValue));
    }

    @NotNull
    public Optional<Setting> get(long guildId, @NotNull String setting) {
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM guild_" + guildId + "_settings WHERE setting = ?")) {
            statement.setString(1, setting);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return Optional.of(Setting.of(guildId, resultSet.getString("setting"), resultSet.getString("value")));
        } catch (SQLException e) {
            throw new RuntimeException("Could not get setting from database", e);
        }
        return Optional.empty();
    }

    public boolean has(long guildId, @NotNull String setting) {
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM guild_" + guildId + "_settings WHERE setting = ?")) {
            statement.setString(1, setting);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get setting from database", e);
        }
    }

    public ConfigReturnValue delete(long guildId, @NotNull String setting) {
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM guild_" + guildId + "_settings WHERE setting = ?")) {
            statement.setString(1, setting);
            return statement.executeUpdate() > 0 ? SUCCESS : NOT_FOUND;
        } catch (SQLException e) {
            return ERROR.setException(e);
        }
    }

    @NotNull
    public List<Setting> getSettings(long guildId) {
        try (Connection con = getConnection(); Statement statement = con.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM guild_" + guildId + "_settings");
            List<Setting> settings = new ArrayList<>(statement.getFetchSize());
            while (resultSet.next())
                settings.add(Setting.of(guildId, resultSet.getString("setting"), resultSet.getString("value")));
            return settings;
        } catch (SQLException e) {
            throw new RuntimeException("Could not get settings from database", e);
        }
    }

    protected void create(long guildId) {
        try (Connection con = getConnection(); Statement statement = con.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS guild_" + guildId + "_settings (" +
                    "setting VARCHAR(255) NOT NULL," +
                    "value VARCHAR(255) NOT NULL" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException("Could not add table to database", e);
        }
    }

    protected void clear(long guildId) {
        try (Connection con = getConnection(); Statement statement = con.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS guild_" + guildId + "_settings");
        } catch (SQLException e) {
            throw new RuntimeException("Could not remove table from database", e);
        }
    }

    public void shutdown() {
        dataSource.close();
    }
}
