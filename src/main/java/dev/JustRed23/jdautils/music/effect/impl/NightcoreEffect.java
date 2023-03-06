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

public final class NightcoreEffect extends AbstractEffect {

    @NotNull
    @Unmodifiable
    protected List<AudioFilter> getEffect(AudioPlayer player, AudioTrack track, @NotNull AudioDataFormat format, UniversalPcmAudioFilter output) {
        TimescalePcmAudioFilter timescale = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate);
        timescale.setSpeed(1.3);
        timescale.setPitch(1.25);
        return Collections.singletonList(timescale);
    }

    @Contract(pure = true)
    public @NotNull String getEffectName() {
        return "nightcore";
    }
}
