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

    private final AudioPlayer player;

    protected AbstractEffect(AudioPlayer player) {
        this.player = player;
    }

    @NotNull
    @Unmodifiable
    abstract List<AudioFilter> getEffect(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output);

    public final void enable() {
        player.setFilterFactory(this::getEffect);
    }

    public final void disable() {
        player.setFilterFactory(null);
    }

    public abstract @NotNull String getEffectName();
}
