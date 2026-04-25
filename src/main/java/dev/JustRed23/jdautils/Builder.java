package dev.JustRed23.jdautils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.JustRed23.jdautils.message.MessageFilter;
import dev.JustRed23.jdautils.music.MusicManager;
import dev.JustRed23.jdautils.music.event.MusicEventListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class Builder {

    boolean ready = false;
    private final ListenerAdapter adapter;

    MusicManager musicManager;

    HikariDataSource dataSource;

    String cachedBotIconUrl;

    Builder() {
        org.sqlite.JDBC.isValidURL(org.sqlite.JDBC.PREFIX); //Call a static JDBC method, this forces the JDBC driver to be loaded
        adapter = new InternalEventListener(this);
    }

    @ApiStatus.Internal
    void ready() {
        ready = true;
    }

    @ApiStatus.Internal
    void destroy() {
        if (dataSource != null)
            dataSource.close();

        if (musicManager != null)
            musicManager.destroy();

        MessageFilter.destroyAll();

        JDAUtilities.builder = null;
    }

    /**
     * Sets the music manager for JDA Utilities to use
     */
    public MusicManagerBuilder withMusicManager() {
        if (ready) throw new IllegalStateException("Music Manager must be set before JDA is built");
        return new MusicManagerBuilder(this);
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

    public MusicManager getMusicManager() {
        if (musicManager == null)
            throw new IllegalStateException("Music Manager has not been set");
        return musicManager;
    }

    /**
     * Builds an immutable configuration snapshot from the current builder state.
     * <p>Call {@link Configuration#configure(JDABuilder)} or {@link Configuration#configure(DefaultShardManagerBuilder)} on the returned snapshot.</p>
     */
    public @NotNull Configuration buildConfiguration() {
        return new Configuration(adapter, musicManager == null ? null : musicManager.getVoiceDispatchInterceptor());
    }

    /**
     * Indicates that JDA has finished loading and JDA Utilities is ready to be used
     * @return Whether JDA Utilities is ready
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Immutable configuration snapshot for applying JDA Utilities to builders.
     */
    public static final class Configuration {

        private final ListenerAdapter adapter;
        private final VoiceDispatchInterceptor voiceDispatchInterceptor;

        private Configuration(ListenerAdapter adapter, VoiceDispatchInterceptor voiceDispatchInterceptor) {
            this.adapter = adapter;
            this.voiceDispatchInterceptor = voiceDispatchInterceptor;
        }

        /**
         * Applies this configuration to a JDA builder.
         */
        public <T extends JDABuilder> T configure(@NotNull T jdaBuilder) {
            jdaBuilder.addEventListeners(adapter);

            if (voiceDispatchInterceptor != null)
                jdaBuilder.setVoiceDispatchInterceptor(voiceDispatchInterceptor);

            return jdaBuilder;
        }

        /**
         * Applies this configuration to a shard manager builder.
         */
        public <T extends DefaultShardManagerBuilder> T configure(@NotNull T shardBuilder) {
            shardBuilder.addEventListeners(adapter);

            if (voiceDispatchInterceptor != null)
                shardBuilder.setVoiceDispatchInterceptor(voiceDispatchInterceptor);

            return shardBuilder;
        }
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

    public static class MusicManagerBuilder {

        private final Builder builder;
        private final List<MusicEventListener> listeners = new ArrayList<>();
        private MusicManager musicManager;

        public MusicManagerBuilder(Builder current) {
            this.builder = current;
        }

        public MusicManagerBuilder useImplementation(@NotNull MusicManager musicManager) {
            if (this.musicManager != null) throw new IllegalStateException("Cannot register multiple music managers");
            this.musicManager = musicManager;
            return this;
        }

        public MusicManagerBuilder addListener(MusicEventListener listener) {
            listeners.add(listener);
            return this;
        }

        public Builder build() {
            if (builder.musicManager != null)
                throw new IllegalStateException("Music Manager has already been set");

            if (musicManager == null)
                throw new IllegalStateException("Music Manager has not been set");

            listeners.forEach(musicManager::addEventListener);

            builder.musicManager = musicManager;
            return builder;
        }
    }
}
