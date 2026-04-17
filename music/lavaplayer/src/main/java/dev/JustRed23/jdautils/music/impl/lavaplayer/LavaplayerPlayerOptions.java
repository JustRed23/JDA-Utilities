package dev.JustRed23.jdautils.music.impl.lavaplayer;

import dev.JustRed23.jdautils.music.GuildPlayerOptions;
import dev.JustRed23.jdautils.music.RepeatMode;
import dev.JustRed23.jdautils.music.TrackDisplayMode;
import dev.JustRed23.jdautils.music.event.VolumeChangeEvent;
import org.jetbrains.annotations.NotNull;

public class LavaplayerPlayerOptions implements GuildPlayerOptions {

    private final LavaplayerGuildMusicManager manager;

    private volatile float volume = 100F;
    private volatile boolean autoDisconnect;
    private volatile TrackDisplayMode trackDisplayMode = TrackDisplayMode.NONE;
    private volatile RepeatMode repeatMode;

    public LavaplayerPlayerOptions(LavaplayerGuildMusicManager manager) {
        this.manager = manager;
    }

    public void setVolume(float volume) {
        if (Float.isNaN(volume) || volume < 0F || volume > 100F) {
            throw new IllegalArgumentException("Volume must be between 0 and 100");
        }

        manager.postEvent(new VolumeChangeEvent(manager.guild().getJDA(), manager.guild(), this.volume, volume));
        this.volume = volume;
        manager.getPlayer().setVolume(Math.round(volume));
    }

    public float getVolume() {
        return this.volume;
    }

    public void setAutoDisconnect(boolean autoDisconnect) {
        this.autoDisconnect = autoDisconnect;
    }

    public boolean isAutoDisconnect() {
        return this.autoDisconnect;
    }

    public void setTrackDisplayMode(@NotNull TrackDisplayMode displayMode) {
        this.trackDisplayMode = displayMode;
    }

    public @NotNull TrackDisplayMode getTrackDisplayMode() {
        return this.trackDisplayMode;
    }

    public void setRepeatMode(@NotNull RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }

    public @NotNull RepeatMode getRepeatMode() {
        return this.repeatMode;
    }
}
