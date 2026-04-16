package dev.JustRed23.jdautils.music.impl.lavalink;

import dev.JustRed23.jdautils.music.PlaybackState;
import dev.JustRed23.jdautils.music.event.MusicEventListener;
import dev.JustRed23.jdautils.music.event.TrackEndEvent;
import dev.JustRed23.jdautils.music.event.TrackErrorEvent;
import dev.JustRed23.jdautils.music.event.TrackStartEvent;
import org.jetbrains.annotations.NotNull;

public class LavalinkInternalGuildEventListener implements MusicEventListener {

    private final LavalinkGuildMusicManager manager;

    public LavalinkInternalGuildEventListener(LavalinkGuildMusicManager manager) {
        this.manager = manager;
    }

    public void onTrackStart(@NotNull TrackStartEvent event) {
        if (!event.guild().equals(manager.guild())) return;
        manager.setTrack(event.track());
        manager.setPosition(0);
        manager.setState(PlaybackState.PLAYING);
    }

    public void onTrackEnd(@NotNull TrackEndEvent event) {
        if (!event.guild().equals(manager.guild())) return;
        if (event.mayStartNext()) {
            manager.getCurrentTrack().ifPresent(track -> ((LavalinkQueue) manager.queue()).addToHistory(track));
            manager.nextTrack();
        } else {
            manager.setTrack(null);
            manager.setState(PlaybackState.IDLE);
        }
    }

    public void onTrackError(@NotNull TrackErrorEvent event) {
        if (!event.guild().equals(manager.guild())) return;
        if (event.track() == null) return; //Track load exception, not a player exception
        manager.stop();
        manager.setState(PlaybackState.ERROR);
    }
}
