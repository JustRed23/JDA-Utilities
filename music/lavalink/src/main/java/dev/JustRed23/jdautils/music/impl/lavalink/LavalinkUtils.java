package dev.JustRed23.jdautils.music.impl.lavalink;

import dev.JustRed23.jdautils.music.PlayableTrack;
import dev.JustRed23.jdautils.music.TrackSource;
import dev.JustRed23.jdautils.music.exception.PlayerException;
import dev.JustRed23.jdautils.music.exception.TrackLoadException;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.client.player.TrackException;
import dev.arbjerg.lavalink.protocol.v4.Exception;
import org.jetbrains.annotations.NotNull;

public final class LavalinkUtils {

    public static @NotNull PlayableTrack fromTrack(Track track) {
        var info = track.getInfo();
        if (info.getUri() == null)
            throw new IllegalArgumentException("Track URI is null");

        return new PlayableTrack(
                TrackSource.get(info.getSourceName()),
                info.getIdentifier(),
                info.getTitle(),
                info.getUri(),
                info.getArtworkUrl(),
                info.getAuthor(),
                null,
                info.isStream() ? 0 : info.getLength(),
                track
        );
    }

    public static @NotNull Throwable fromLavalinkException(TrackException exception) {
        final Exception.Severity severity = exception.getSeverity();
        Throwable ex;
        if (severity.equals(Exception.Severity.COMMON))
            ex = new TrackLoadException(exception.getMessage(), new Throwable(exception.getCause()));
        else
            ex = new PlayerException(exception.getMessage(), new Throwable(exception.getCause()));

        return ex;
    }
}
