package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a playlist of {@link PlayableTrack tracks} with a name and optional description.
 */
public class TrackList {

    private final List<PlayableTrack> tracks;
    private final String name;
    private final @Nullable String description;

    /**
     * Creates a new track list with the given name.
     *
     * @param name The name of the playlist.
     */
    public TrackList(@NotNull String name) {
        this(name, null);
    }

    /**
     * Creates a new track list with the given name and description.
     *
     * @param name The name of the playlist.
     * @param description Optional description for the playlist.
     */
    public TrackList(@NotNull String name, @Nullable String description) {
        this(name, description, List.of());
    }

    /**
     * Creates a new track list with the given name, description, and tracks.
     *
     * @param name The name of the playlist.
     * @param description Optional description for the playlist.
     * @param tracks The initial tracks in the playlist.
     */
    public TrackList(@NotNull String name, @Nullable String description, @NotNull List<PlayableTrack> tracks) {
        this.name = name;
        this.description = description;
        this.tracks = new ArrayList<>(tracks);
    }

    /**
     * Adds a track to this playlist.
     *
     * @param track The track to add.
     */
    public void add(@NotNull PlayableTrack track) {
        tracks.add(track);
    }

    /**
     * Adds multiple tracks to this playlist.
     *
     * @param tracks The tracks to add.
     */
    public void addAll(@NotNull List<PlayableTrack> tracks) {
        this.tracks.addAll(tracks);
    }

    /**
     * Removes a track at the specified index.
     *
     * @param index The index of the track to remove.
     */
    public void remove(int index) {
        tracks.remove(index);
    }

    /**
     * Removes a specific track from this playlist.
     *
     * @param track The track to remove.
     */
    public void remove(@NotNull PlayableTrack track) {
        tracks.remove(track);
    }

    /**
     * Clears all tracks from this playlist.
     */
    public void clear() {
        tracks.clear();
    }

    /**
     * Shuffles the tracks in this playlist.
     */
    public void shuffle() {
        Collections.shuffle(tracks);
    }

    /**
     * Gets an unmodifiable list of all tracks in this playlist.
     *
     * @return The list of tracks.
     */
    public @NotNull List<PlayableTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    /**
     * Gets the number of tracks in this playlist.
     *
     * @return The number of tracks.
     */
    public int size() {
        return tracks.size();
    }

    /**
     * Checks if this playlist is empty.
     *
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    /**
     * Gets the track at the specified index.
     *
     * @param index The index of the track.
     * @return The track at the index.
     */
    public PlayableTrack get(int index) {
        return tracks.get(index);
    }

    /**
     * Gets the name of this playlist.
     *
     * @return The playlist name.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the description of this playlist.
     *
     * @return The description, or null if not set.
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Checks if this playlist has a description.
     *
     * @return true if description exists and is not blank, false otherwise.
     */
    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    /**
     * Gets the total duration of all tracks in this playlist.
     * <p>
     * Only includes tracks with known duration. Live streams and tracks with unknown duration are excluded.
     *
     * @return The total duration in milliseconds.
     */
    public long getTotalDuration() {
        return tracks.stream()
                .filter(PlayableTrack::hasDuration)
                .mapToLong(PlayableTrack::durationMillis)
                .sum();
    }
}
