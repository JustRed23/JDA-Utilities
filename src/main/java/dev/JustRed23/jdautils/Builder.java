package dev.JustRed23.jdautils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.JustRed23.jdautils.message.MessageFilter;
import dev.JustRed23.jdautils.music.AudioManager;
import dev.JustRed23.jdautils.music.effect.AbstractEffect;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public final class Builder {

    boolean ready = false;
    private final ListenerAdapter adapter;

    private HikariDataSource dataSource;

    String cachedBotIconUrl;

    Builder() {
        org.sqlite.JDBC.isValidURL(org.sqlite.JDBC.PREFIX); //Call a static JDBC method, this forces the JDBC driver to be loaded
        adapter = new InternalEventListener(this);
    }

    @ApiStatus.Internal
    void ready() {
        Reflections reflections = new Reflections(AbstractEffect.class.getPackageName() + ".impl");
        reflections.getSubTypesOf(AbstractEffect.class).forEach(clazz -> {
            try {
                clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while trying to register default effects", e);
            }
        });

        ready = true;
    }

    @ApiStatus.Internal
    void destroy() {
        if (dataSource != null)
            dataSource.close();

        AudioManager.destroyAll();
        MessageFilter.destroyAll();

        JDAUtilities.builder = null;
    }

    /**
     * Sets the database for JDA Utilities to use
     */
    public DBBuilder withDatabase() {
        if (ready) throw new IllegalStateException("Database must be set before JDA is built");
        return new DBBuilder(this);
    }

    /**
     * Gets the database connection, you should use this to get a connection to the database
     * <br><b>Do not forget to close the connection when you are done with it, use a try-with-resources statement</b>
     * @return The database connection
     * @throws SQLException If a database access error occurs
     */
    public Connection getDatabaseConnection() throws SQLException {
        if (dataSource == null)
            throw new IllegalStateException("Database has not been set");
        return dataSource.getConnection();
    }

    /**
     * Gets the internal event listener, you need to add this to your JDA instance
     * @see JDA#addEventListener(Object...)
     */
    public ListenerAdapter listener() {
        return adapter;
    }

    /**
     * Indicates that JDA has finished loading and JDA Utilities is ready to be used
     * @return Whether JDA Utilities is ready
     */
    public boolean isReady() {
        return ready;
    }

    public static class DBBuilder {

        private final Builder builder;
        private final HikariConfig config;

        private DBBuilder(Builder current) {
            this.builder = current;

            config = new HikariConfig();
            config.setConnectionTestQuery("SELECT 1");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        }

        public Builder fileBased(@NotNull File file) {
            config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());
            return build();
        }

        public Builder fileBased(@NotNull String fileName) {
            return fileBased(new File(fileName));
        }

        public Builder remote(@NotNull String url, @NotNull String user, @NotNull String password) {
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            return build();
        }

        private Builder build() {
            if (builder.dataSource != null)
                throw new IllegalStateException("Database has already been set");

            builder.dataSource = new HikariDataSource(config);
            return builder;
        }
    }
}
