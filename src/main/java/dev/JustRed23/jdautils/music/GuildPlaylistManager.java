package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

/**
 * Manages playlists for a guild.
 */
public interface GuildPlaylistManager {

    /**
     * Creates a new playlist with the given name.
     *
     * @param name The name of the playlist.
     * @return The created TrackList.
     */
    @NotNull TrackList createPlaylist(@NotNull String name);

    /**
     * Creates a new playlist with the given name and description.
     *
     * @param name The name of the playlist.
     * @param description Optional description for the playlist.
     * @return The created TrackList.
     */
    @NotNull TrackList createPlaylist(@NotNull String name, @Nullable String description);

    /**
     * Retrieves a playlist by name.
     *
     * @param name The name of the playlist.
     * @return An Optional containing the playlist, or empty if not found.
     */
    @NotNull Optional<TrackList> getPlaylist(@NotNull String name);

    /**
     * Gets all playlists.
     *
     * @return A collection of all playlists.
     */
    @NotNull Collection<TrackList> getAllPlaylists();

    /**
     * Deletes a playlist by name.
     *
     * @param name The name of the playlist to delete.
     */
    void deletePlaylist(@NotNull String name);

    /**
     * Checks if a playlist exists.
     *
     * @param name The name of the playlist.
     * @return true if the playlist exists, false otherwise.
     */
    default boolean playlistExists(@NotNull String name) {
        return getPlaylist(name).isPresent();
    }

    /**
     * Gets the total number of playlists.
     *
     * @return The number of playlists.
     */
    default int getPlaylistCount() {
        return getAllPlaylists().size();
    }

    /**
     * Deletes all playlists.
     */
    void clearAll();

    /**
     * Persists all playlists to storage.
     */
    void save();

    /**
     * Loads all playlists from storage.
     */
    void load();
}
