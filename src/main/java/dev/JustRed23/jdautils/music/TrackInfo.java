package dev.JustRed23.jdautils.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.JustRed23.jdautils.utils.TimeUtils;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public record TrackInfo(Guild guild, AudioTrack track, String source) {

    @NotNull
    @Contract("_, _ -> new")
    public static TrackInfo of(Guild guild, AudioTrack track) {
        return new TrackInfo(guild, track, track.getSourceManager() != null ? track.getSourceManager().getSourceName() : "unknown");
    }

    public long getTimestamp(@NotNull TimeUnit unit) {
        return unit.convert(track.getPosition(), TimeUnit.MILLISECONDS);
    }

    /**
     * @return The current timestamp of the track in the format of (dd:)(hh:)mm:ss
     */
    @NotNull
    public String getTimestamp() {
        return TimeUtils.millisToTime(track.getPosition());
    }
}
