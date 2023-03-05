package dev.JustRed23.jdautils.music.effect;

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public final class SpeedEffect extends AbstractEffect {

    private final float speed;

    public SpeedEffect(float speed) {
        this.speed = speed;
    }

    @NotNull
    @Unmodifiable
    List<AudioFilter> getEffect(AudioPlayer player, AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
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
