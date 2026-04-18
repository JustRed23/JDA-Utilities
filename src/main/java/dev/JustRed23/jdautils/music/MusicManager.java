package dev.JustRed23.jdautils.music;

import dev.JustRed23.jdautils.music.event.MusicEventListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Central manager for music playback across all guilds.
 * <p>
 * Provides access to guild-specific music managers and handles global music events.
 * Implementations should manage the lifecycle of GuildMusicManager instances and coordinate
 * music playback across different guilds.
 */
public interface MusicManager {

    /**
     * Gets the music manager for a specific guild.
     * <p>
     * Creates or retrieves an existing GuildMusicManager instance for the given guild.
     * Each guild has its own isolated music manager.
     *
     * @param guild The guild to get the music manager for.
     * @return The GuildMusicManager for the specified guild.
     */
    GuildMusicManager forGuild(@NotNull Guild guild);

    /**
     * Adds a global music event listener.
     * <p>
     * The listener will receive events from all guilds managed by this MusicManager.
     *
     * @param listener The event listener to add.
     */
    void addEventListener(@NotNull MusicEventListener listener);

    /**
     * Removes a global music event listener.
     *
     * @param listener The event listener to remove.
     */
    void removeEventListener(@NotNull MusicEventListener listener);

    /**
     * Returns the voice dispatch interceptor used by this music manager, if any.
     * <p>
     * Implementations that do not need voice dispatch interception can return {@code null}.
     * The interceptor is used to handle voice-related events and commands.
     *
     * @return The voice dispatch interceptor, or {@code null} if not needed.
     */
    default @Nullable VoiceDispatchInterceptor getVoiceDispatchInterceptor() {
        return null;
    }

    /**
     * Destroys this music manager and releases all resources.
     * <p>
     * This should clean up all guild music managers, disconnect from all voice channels,
     * and stop all playback. After calling this method, the MusicManager should not be used.
     */
    void destroy();
}
