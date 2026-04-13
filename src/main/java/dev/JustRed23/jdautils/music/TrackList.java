package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackList {

    private final List<PlayableTrack> tracks;
    private final String name;
    private final @Nullable String description;

    public TrackList(@NotNull String name) {
        this(name, null);
    }

    public TrackList(@NotNull String name, @Nullable String description) {
        this(name, description, List.of());
    }

    public TrackList(@NotNull String name, @Nullable String description, @NotNull List<PlayableTrack> tracks) {
        this.name = name;
        this.description = description;
        this.tracks = new ArrayList<>(tracks);
    }

    public void add(@NotNull PlayableTrack track) {
        tracks.add(track);
    }

    public void addAll(@NotNull List<PlayableTrack> tracks) {
        this.tracks.addAll(tracks);
    }

    public void remove(int index) {
        tracks.remove(index);
    }

    public void remove(@NotNull PlayableTrack track) {
        tracks.remove(track);
    }

    public void clear() {
        tracks.clear();
    }

    public void shuffle() {
        Collections.shuffle(tracks);
    }

    public @NotNull List<PlayableTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public int size() {
        return tracks.size();
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    public PlayableTrack get(int index) {
        return tracks.get(index);
    }

    public @NotNull String getName() {
        return name;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public long getTotalDuration() {
        return tracks.stream()
                .filter(PlayableTrack::hasDuration)
                .mapToLong(PlayableTrack::durationMillis)
                .sum();
    }
}
