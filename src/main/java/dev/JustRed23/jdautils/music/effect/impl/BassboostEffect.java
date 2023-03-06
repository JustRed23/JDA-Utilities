package dev.JustRed23.jdautils.music.effect.impl;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.JustRed23.jdautils.music.effect.AbstractEffect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public final class BassboostEffect extends AbstractEffect {

    private final float[] freqGain = {-0.05f, 0.07f, 0.16f, 0.03f, -0.05f, -0.11f};

    private float multiplier = 1;

    public AbstractEffect setValue(Object value) {
        if (value instanceof Float)
            multiplier = (float) value;
        else
            throw new IllegalArgumentException("Value must be a float");
        return this;
    }

    @NotNull
    @Unmodifiable
    protected List<AudioFilter> getEffect(AudioPlayer player, AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
        if (multiplier == 1)
            return Collections.emptyList();
        else {
            EqualizerFactory factory = new EqualizerFactory();

            for (int i = 0; i < freqGain.length; i++)
                factory.setGain(i, freqGain[i] * multiplier);

            return factory.buildChain(track, format, output);
        }
    }

    @Contract(pure = true)
    public @NotNull String getEffectName() {
        return "bassboost";
    }
}
