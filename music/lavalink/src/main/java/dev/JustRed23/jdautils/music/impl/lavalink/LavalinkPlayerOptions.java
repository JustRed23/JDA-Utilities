package dev.JustRed23.jdautils.music.impl.lavalink;

import dev.JustRed23.jdautils.music.GuildPlayerOptions;
import dev.JustRed23.jdautils.music.RepeatMode;
import dev.JustRed23.jdautils.music.TrackDisplayMode;
import dev.JustRed23.jdautils.music.event.VolumeChangeEvent;
import org.jetbrains.annotations.NotNull;

public class LavalinkPlayerOptions implements GuildPlayerOptions {

    private final LavalinkGuildMusicManager manager;

    private volatile int volume = 100;
    private volatile boolean autoDisconnect;
    private volatile TrackDisplayMode trackDisplayMode = TrackDisplayMode.NONE;
    private volatile RepeatMode repeatMode = RepeatMode.OFF;

    public LavalinkPlayerOptions(LavalinkGuildMusicManager manager) {
        this.manager = manager;
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) {
            throw new IllegalArgumentException("Volume must be between 0 and 100");
        }

        manager.postEvent(new VolumeChangeEvent(manager.guild().getJDA(), manager.guild(), this.volume, volume));
        this.volume = volume;
        manager.getLink().getPlayer().subscribe(player -> player.setVolume(volume).subscribe());
    }

    public int getVolume() {
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
