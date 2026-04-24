package dev.JustRed23.jdautils.music;

import dev.JustRed23.jdautils.utils.TimeUtils;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents an audio track that can be played.
 * <p>
 * Contains metadata about the track such as title, URL, duration, and optional fields like author and album.
 * Provides utility methods for checking track properties and formatting information.
 *
 * @param source The source of the track (e.g., YouTube, Spotify).
 * @param id An optional unique identifier for the track from its source.
 * @param title The title of the track.
 * @param url The URL where the track can be accessed.
 * @param thumbnailUrl An optional URL for the track's thumbnail image.
 * @param author An optional author or artist of the track.
 * @param album An optional album name for the track.
 * @param durationMillis The duration of the track in milliseconds, or -1 if unknown, or 0 if it's a live stream.
 * @param member The member who requested the track.
 * @param raw An optional raw data object from the source, which can be used for advanced features or debugging.
 */
public record PlayableTrack(
        @NotNull TrackSource source,
        @Nullable String id,
        @NotNull String title,
        @NotNull String url,
        @Nullable String thumbnailUrl,
        @Nullable String author,
        @Nullable String album,
        long durationMillis,
        @NotNull Member member,
        @Nullable Object raw
) {

    public PlayableTrack {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(url, "url");

        if (durationMillis < -1)
            throw new IllegalArgumentException("Duration must be -1 or greater");
    }

    /**
     * Checks if this track has a known duration.
     *
     * @return true if duration is known and greater than 0, false otherwise.
     */
    @Contract(pure = true)
    public boolean hasDuration() {
        return durationMillis > 0;
    }

    /**
     * Checks if this track is a live stream.
     *
     * @return true if duration is 0 (live stream), false otherwise.
     */
    @Contract(pure = true)
    public boolean isLiveStream() {
        return durationMillis == 0;
    }

    /**
     * Checks if this track has an unknown duration.
     *
     * @return true if duration is negative, false otherwise.
     */
    @Contract(pure = true)
    public boolean isUnknownDuration() {
        return durationMillis < 0;
    }

    /**
     * Checks if this track has a thumbnail URL.
     *
     * @return true if a thumbnail URL is available and not blank, false otherwise.
     */
    @Contract(pure = true)
    public boolean hasThumbnail() {
        return thumbnailUrl != null && !thumbnailUrl.isBlank();
    }

    /**
     * Checks if this track has an author.
     *
     * @return true if an author is available and not blank, false otherwise.
     */
    @Contract(pure = true)
    public boolean hasAuthor() {
        return author != null && !author.isBlank();
    }

    /**
     * Checks if this track has an album.
     *
     * @return true if an album is available and not blank, false otherwise.
     */
    @Contract(pure = true)
    public boolean hasAlbum() {
        return album != null && !album.isBlank();
    }

    /**
     * Gets the duration formatted as a human-readable string.
     * <p>
     * Returns "LIVE" for live streams, "UNKNOWN" for unknown duration, or formatted time otherwise.
     *
     * @return The formatted duration string.
     */
    @Contract(pure = true)
    public @NotNull String formattedDuration() {
        if (isUnknownDuration()) {
            return "UNKNOWN";
        }

        return isLiveStream() ? "LIVE" : TimeUtils.millisToTime(durationMillis);
    }

    /**
     * Creates a new PlayableTrack with the specified raw data.
     *
     * @param raw The raw data object to attach.
     * @return A new PlayableTrack with the raw data set.
     */
    @Contract(pure = true)
    public @NotNull PlayableTrack withRaw(@Nullable Object raw) {
        return new PlayableTrack(source, id, title, url, thumbnailUrl, author, album, durationMillis, member, raw);
    }

    /**
     * Gets a display name for this track.
     * <p>
     * Returns "Author - Title" if author is available, otherwise just the title.
     *
     * @return The display name for this track.
     */
    @Contract(pure = true)
    public @NotNull String getDisplayName() {
        if (hasAuthor()) {
            return author + " - " + title;
        }
        return title;
    }

    /**
     * Checks if this track is the same as another track.
     * <p>
     * Compares by source, then by ID if available, otherwise by URL.
     *
     * @param other The other track to compare with.
     * @return true if the tracks are the same, false otherwise.
     */
    @Contract(pure = true)
    public boolean isSameTrack(@NotNull PlayableTrack other) {
        if (source != other.source) {
            return false;
        }
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        return url.equals(other.url);
    }

    /**
     * Checks if this track matches the given search query.
     * <p>
     * Searches case-insensitively in title, author, album, and URL.
     *
     * @param query The search query string.
     * @return true if the track matches the query, false otherwise.
     */
    @Contract(pure = true)
    public boolean matches(@NotNull String query) {
        String lowerQuery = query.toLowerCase();
        return title.toLowerCase().contains(lowerQuery) ||
               (author != null && author.toLowerCase().contains(lowerQuery)) ||
               (album != null && album.toLowerCase().contains(lowerQuery)) ||
               url.toLowerCase().contains(lowerQuery);
    }
}
