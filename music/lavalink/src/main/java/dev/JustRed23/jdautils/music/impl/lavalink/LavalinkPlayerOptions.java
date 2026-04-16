package dev.JustRed23.jdautils.music.impl.lavalink;

import dev.JustRed23.jdautils.music.GuildPlayerOptions;
import dev.JustRed23.jdautils.music.RepeatMode;
import dev.JustRed23.jdautils.music.event.VolumeChangeEvent;
import org.jetbrains.annotations.NotNull;

public class LavalinkPlayerOptions implements GuildPlayerOptions {

    private final LavalinkGuildMusicManager manager;

    private volatile float volume = 100F;
    private volatile boolean autoDisconnect;
    private volatile boolean updateUserStatusWithSongInfo;
    private volatile RepeatMode repeatMode;

    public LavalinkPlayerOptions(LavalinkGuildMusicManager manager) {
        this.manager = manager;
    }

    public void setVolume(float volume) {
        if (Float.isNaN(volume) || volume < 0F || volume > 100F) {
            throw new IllegalArgumentException("Volume must be between 0 and 100");
        }

        manager.postEvent(new VolumeChangeEvent(manager.guild().getJDA(), manager.guild(), this.volume, volume));
        this.volume = volume;
        manager.getLink().getPlayer().subscribe(player -> player.setVolume(Math.round(volume)).subscribe());
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

    public void updateUserStatusWithSongInfo(boolean enabled) {
        this.updateUserStatusWithSongInfo = enabled;
    }

    public boolean isUpdateUserStatusWithSongInfo() {
        return this.updateUserStatusWithSongInfo;
    }

    public void setRepeatMode(@NotNull RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }

    public @NotNull RepeatMode getRepeatMode() {
        return this.repeatMode;
    }
}
