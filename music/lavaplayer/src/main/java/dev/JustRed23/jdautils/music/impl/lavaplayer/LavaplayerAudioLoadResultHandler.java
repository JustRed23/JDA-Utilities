package dev.JustRed23.jdautils.music.impl.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.JustRed23.jdautils.music.PlayableTrack;
import dev.JustRed23.jdautils.music.PlaybackState;
import dev.JustRed23.jdautils.music.event.QueueUpdateEvent;
import dev.JustRed23.jdautils.music.event.TrackErrorEvent;
import dev.JustRed23.jdautils.music.event.TrackNotFoundEvent;

import java.util.List;

import static dev.JustRed23.jdautils.music.impl.lavaplayer.LavaplayerUtils.fromTrack;

public class LavaplayerAudioLoadResultHandler implements AudioLoadResultHandler {

    private final LavaplayerGuildMusicManager manager;
    private final LavaplayerQueue queue;
    private final PlaybackState prevState;
    private final String url;

    public LavaplayerAudioLoadResultHandler(LavaplayerGuildMusicManager manager, PlaybackState prevState, String url) {
        this.manager = manager;
        queue = (LavaplayerQueue) manager.queue();
        this.prevState = prevState;
        this.url = url;
    }

    public void trackLoaded(AudioTrack audioTrack) {
        PlayableTrack track = fromTrack(audioTrack);
        queue.addToQueue(track);
        manager.postEvent(new QueueUpdateEvent(manager.guild().getJDA(), manager.guild(), QueueUpdateEvent.QueueUpdateType.ADDED, List.of(track), queue.getQueue().size()));
    }

    public void playlistLoaded(AudioPlaylist playlist) {
        List<PlayableTrack> tracks = playlist.getTracks().stream().map(LavaplayerUtils::fromTrack).toList();
        if (tracks.isEmpty()) return;

        if (playlist.isSearchResult()) {
            queue.addToQueue(tracks.get(0));
            manager.postEvent(new QueueUpdateEvent(manager.guild().getJDA(), manager.guild(), QueueUpdateEvent.QueueUpdateType.ADDED, List.of(tracks.get(0)), queue.getQueue().size()));
        } else {
            queue.addAllToQueue(tracks);
            manager.postEvent(new QueueUpdateEvent(manager.guild().getJDA(), manager.guild(), QueueUpdateEvent.QueueUpdateType.ADDED_PLAYLIST, tracks, queue.getQueue().size()));
        }
    }

    public void noMatches() {
        manager.postEvent(new TrackNotFoundEvent(manager.guild().getJDA(), manager.guild(), url));
        manager.setState(prevState);
    }

    public void loadFailed(FriendlyException exception) {
        manager.postEvent(new TrackErrorEvent(manager.guild().getJDA(), manager.guild(), null, exception));
        manager.setState(prevState);
    }
}
