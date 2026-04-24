package dev.JustRed23.jdautils.music.impl.lavaplayer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.JustRed23.jdautils.music.PlayableTrack;
import dev.JustRed23.jdautils.music.TrackSource;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public final class LavaplayerUtils {

    public static @NotNull PlayableTrack fromTrack(AudioTrack track, Member member) {
        var info = track.getInfo();
        if (info.uri == null)
            throw new IllegalArgumentException("Track URI is null");

        var source = TrackSource.UNKNOWN;
        if (track.getSourceManager() != null)
            source = TrackSource.get(track.getSourceManager().getSourceName());

        return new PlayableTrack(
                source,
                info.identifier,
                info.title,
                info.uri,
                info.artworkUrl,
                info.author,
                null,
                info.isStream ? 0 : info.length,
                member,
                track
        );
    }
}
