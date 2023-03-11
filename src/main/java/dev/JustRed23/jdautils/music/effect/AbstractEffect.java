package dev.JustRed23.jdautils.music.effect;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractEffect {

    private static final Map<String, AbstractEffect> EFFECTS = new ConcurrentHashMap<>();
    private static final List<String> DISABLED_EFFECTS = new ArrayList<>();

    public static void registerEffect(@NotNull AbstractEffect effect) {
        LoggerFactory.getLogger(AbstractEffect.class).info("Registered audio effect: " + effect.getEffectName());
        EFFECTS.put(effect.getEffectName(), effect);
    }

    public static void enableEffect(@NotNull String name) {
        DISABLED_EFFECTS.remove(name);
    }

    public static void disableEffect(@NotNull String name) {
        DISABLED_EFFECTS.add(name);
    }

    public static AbstractEffect getEffect(String name) {
        return EFFECTS.get(name);
    }

    protected AbstractEffect() {
        registerEffect(this);
    }

    @NotNull
    @Unmodifiable
    protected abstract List<AudioFilter> getEffect(AudioPlayer player, AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output);

    public AbstractEffect setValue(Object value) {
        return this;
    }

    public final void enable(@NotNull AudioPlayer player) {
        if (DISABLED_EFFECTS.contains(getEffectName()))
            return;

        player.setFilterFactory((track, format, output) -> {
            final List<AudioFilter> effect = getEffect(player, track, format, output);
            if (effect.isEmpty())
                return null;
            return effect;
        });
    }

    public final void disable(@NotNull AudioPlayer player) {
        if (DISABLED_EFFECTS.contains(getEffectName()))
            return;

        player.setFilterFactory(null);
    }

    public abstract @NotNull String getEffectName();
}
