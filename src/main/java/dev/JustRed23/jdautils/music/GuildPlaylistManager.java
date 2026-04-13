package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface GuildPlaylistManager {

    @NotNull TrackList createPlaylist(@NotNull String name);
    @NotNull TrackList createPlaylist(@NotNull String name, @Nullable String description);

    @NotNull Optional<TrackList> getPlaylist(@NotNull String name);
    @NotNull Collection<TrackList> getAllPlaylists();

    void deletePlaylist(@NotNull String name);
    boolean playlistExists(@NotNull String name);
    int getPlaylistCount();
    void clearAll();

    void save();
    void load();
}
