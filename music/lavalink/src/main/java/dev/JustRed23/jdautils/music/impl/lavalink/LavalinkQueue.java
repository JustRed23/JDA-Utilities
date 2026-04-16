package dev.JustRed23.jdautils.music.impl.lavalink;

import dev.JustRed23.jdautils.music.GuildQueueOptions;
import dev.JustRed23.jdautils.music.PlayableTrack;
import dev.JustRed23.jdautils.music.PlaybackState;
import dev.JustRed23.jdautils.music.RepeatMode;
import dev.arbjerg.lavalink.client.player.Track;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LavalinkQueue implements GuildQueueOptions {

    private final List<PlayableTrack> queue = new CopyOnWriteArrayList<>();
    private final List<PlayableTrack> history = new CopyOnWriteArrayList<>();
    private final LavalinkGuildMusicManager manager;

    public LavalinkQueue(LavalinkGuildMusicManager musicManager) {
        this.manager = musicManager;
    }

    @ApiStatus.Internal
    void addToQueue(@NotNull PlayableTrack track) {
        queue.add(track);
        if (manager.getCurrentTrack().isEmpty()) nextTrack();
    }

    @ApiStatus.Internal
    void addAllToQueue(@NotNull List<PlayableTrack> tracks) {
        queue.addAll(tracks);
        if (manager.getCurrentTrack().isEmpty()) nextTrack();
    }

    @ApiStatus.Internal
    void nextTrack() {
        if (queue.isEmpty()) {
            RepeatMode mode = manager.options().getRepeatMode();
            if (mode == RepeatMode.ONE && manager.getCurrentTrack().isPresent()) {
                PlayableTrack current = manager.getCurrentTrack().get();
                manager.getLink().getPlayer().subscribe(player -> player.setTrack((Track) current.raw()).subscribe());
                return;
            } else if (mode == RepeatMode.ALL && !history.isEmpty()) {
                queue.addAll(history);
                history.clear();
            } else {
                manager.setTrack(null);
                manager.setState(PlaybackState.IDLE);
                return;
            }
        }

        PlayableTrack track = queue.remove(0);
        manager.setTrack(track);
        manager.getLink().getPlayer().subscribe(player -> player.setTrack((Track) track.raw()).subscribe());
    }

    public void skip() {
        manager.getCurrentTrack().ifPresent(history::add);

        if (queue.isEmpty()) {
            manager.stop();
            return;
        }

        PlayableTrack next = queue.remove(0);
        manager.setTrack(next);
        manager.getLink().getPlayer().subscribe(player -> player.setTrack((Track) next.raw()).subscribe());
        manager.setState(PlaybackState.PLAYING);
    }

    public void back() {
        if (history.isEmpty()) return;
        manager.getCurrentTrack().ifPresent(track -> queue.add(0, track));
        PlayableTrack previous = history.remove(history.size() - 1);
        manager.setTrack(previous);
        manager.getLink().getPlayer().subscribe(player -> player.setTrack((Track) previous.raw()).subscribe());
        manager.setState(PlaybackState.PLAYING);
    }

    @ApiStatus.Internal
    void addToHistory(@NotNull PlayableTrack track) {
        history.add(track);
    }

    public void clear() {
        queue.clear();
        history.clear();
    }

    public void shuffle() {
        Collections.shuffle(queue);
    }

    public @NotNull List<PlayableTrack> getQueue() {
        return List.copyOf(queue);
    }

    public @NotNull List<PlayableTrack> getHistory() {
        return List.copyOf(history);
    }
}
