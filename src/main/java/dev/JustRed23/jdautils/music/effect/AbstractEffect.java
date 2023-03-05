package dev.JustRed23.jdautils.music.effect;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public abstract class AbstractEffect {

    @NotNull
    @Unmodifiable
    abstract List<AudioFilter> getEffect(AudioPlayer player, AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output);

    public final void enable(@NotNull AudioPlayer player) {
        player.setFilterFactory((track, format, output) -> getEffect(player, track, format, output));
    }

    public final void disable(@NotNull AudioPlayer player) {
        player.setFilterFactory(null);
    }

    public abstract @NotNull String getEffectName();
}
