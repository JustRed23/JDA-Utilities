package dev.JustRed23.jdautils.music.effect.impl;

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.JustRed23.jdautils.music.effect.AbstractEffect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public final class SpeedEffect extends AbstractEffect {

    private float speed = 1;

    public AbstractEffect setValue(Object value) {
        if (value instanceof Float)
            speed = (float) value;
        else
            throw new IllegalArgumentException("Value must be a float");
        return this;
    }

    @NotNull
    @Unmodifiable
    protected List<AudioFilter> getEffect(AudioPlayer player, AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
        if (speed <= 0 || speed == 1)
            return Collections.emptyList();

        TimescalePcmAudioFilter timescale = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate);
        timescale.setSpeed(speed);
        return Collections.singletonList(timescale);
    }

    @Contract(pure = true)
    public @NotNull String getEffectName() {
        return "speed";
    }
}
