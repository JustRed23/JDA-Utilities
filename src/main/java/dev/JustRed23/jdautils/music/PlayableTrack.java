package dev.JustRed23.jdautils.music;

import dev.JustRed23.jdautils.utils.TimeUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record PlayableTrack(
        @NotNull TrackSource source,
        @Nullable String id,
        @NotNull String title,
        @NotNull String url,
        @Nullable String thumbnailUrl,
        @Nullable String author,
        @Nullable String album,
        long durationMillis,
        @Nullable Object raw
) {

    public PlayableTrack {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(url, "url");

        if (durationMillis < -1)
            throw new IllegalArgumentException("Duration must be -1 or greater");
    }

    @Contract(pure = true)
    public boolean hasDuration() {
        return durationMillis > 0;
    }

    @Contract(pure = true)
    public boolean isLiveStream() {
        return durationMillis == 0;
    }

    @Contract(pure = true)
    public boolean isUnknownDuration() {
        return durationMillis < 0;
    }

    @Contract(pure = true)
    public boolean hasThumbnail() {
        return thumbnailUrl != null && !thumbnailUrl.isBlank();
    }

    @Contract(pure = true)
    public boolean hasAuthor() {
        return author != null && !author.isBlank();
    }

    @Contract(pure = true)
    public boolean hasAlbum() {
        return album != null && !album.isBlank();
    }

    @Contract(pure = true)
    public @NotNull String formattedDuration() {
        if (isUnknownDuration()) {
            return "UNKNOWN";
        }

        return isLiveStream() ? "LIVE" : TimeUtils.millisToTime(durationMillis);
    }

    @Contract(pure = true)
    public @NotNull PlayableTrack withRaw(@Nullable Object raw) {
        return new PlayableTrack(source, id, title, url, thumbnailUrl, author, album, durationMillis, raw);
    }

    @Contract(pure = true)
    public @NotNull String getDisplayName() {
        if (hasAuthor()) {
            return author + " - " + title;
        }
        return title;
    }

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

    @Contract(pure = true)
    public boolean matches(@NotNull String query) {
        String lowerQuery = query.toLowerCase();
        return title.toLowerCase().contains(lowerQuery) ||
               (author != null && author.toLowerCase().contains(lowerQuery)) ||
               (album != null && album.toLowerCase().contains(lowerQuery)) ||
               url.toLowerCase().contains(lowerQuery);
    }
}
