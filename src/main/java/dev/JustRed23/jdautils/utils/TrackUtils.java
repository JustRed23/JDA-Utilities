package dev.JustRed23.jdautils.utils;

import dev.JustRed23.jdautils.music.PlayableTrack;
import dev.JustRed23.jdautils.music.TrackList;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public final class TrackUtils {

    private TrackUtils() {}

    public static @NotNull List<PlayableTrack> sortByTitle(@NotNull List<PlayableTrack> tracks) {
        return tracks.stream()
                .sorted(Comparator.comparing(PlayableTrack::title))
                .toList();
    }

    public static @NotNull List<PlayableTrack> sortByAuthor(@NotNull List<PlayableTrack> tracks) {
        return tracks.stream()
                .sorted(Comparator.comparing(t -> t.author() != null ? t.author() : ""))
                .toList();
    }

    public static @NotNull List<PlayableTrack> sortByDuration(@NotNull List<PlayableTrack> tracks) {
        return tracks.stream()
                .sorted(Comparator.comparingLong(PlayableTrack::durationMillis))
                .toList();
    }

    public static @NotNull List<PlayableTrack> sortByDurationDesc(@NotNull List<PlayableTrack> tracks) {
        return tracks.stream()
                .sorted(Comparator.comparingLong(PlayableTrack::durationMillis).reversed())
                .toList();
    }

    public static @NotNull List<PlayableTrack> filterByAuthor(@NotNull List<PlayableTrack> tracks, @NotNull String author) {
        return tracks.stream()
                .filter(t -> t.author() != null && t.author().equalsIgnoreCase(author))
                .toList();
    }

    public static @NotNull List<PlayableTrack> filterExplicit(@NotNull List<PlayableTrack> tracks, boolean includeExplicit) {
        return tracks.stream()
                .filter(t -> t.explicit() == includeExplicit)
                .toList();
    }

    public static @NotNull List<String> getUniqueAuthors(@NotNull List<PlayableTrack> tracks) {
        return tracks.stream()
                .map(PlayableTrack::author)
                .filter(author -> author != null && !author.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    public static @NotNull List<PlayableTrack> search(@NotNull List<PlayableTrack> tracks, @NotNull String query) {
        return tracks.stream()
                .filter(t -> t.matches(query))
                .toList();
    }

    public static long calculateTotalDuration(@NotNull List<PlayableTrack> tracks) {
        return tracks.stream()
                .filter(PlayableTrack::hasDuration)
                .mapToLong(PlayableTrack::durationMillis)
                .sum();
    }

    public static @NotNull TrackList toTrackList(@NotNull String name, @NotNull List<PlayableTrack> tracks) {
        TrackList list = new TrackList(name);
        list.addAll(tracks);
        return list;
    }

    public static @NotNull TrackList combine(@NotNull String name, @NotNull TrackList... lists) {
        TrackList combined = new TrackList(name);
        for (TrackList list : lists) {
            combined.addAll(list.getTracks());
        }
        return combined;
    }
}
