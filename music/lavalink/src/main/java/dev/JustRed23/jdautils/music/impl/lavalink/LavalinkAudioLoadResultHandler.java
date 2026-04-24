package dev.JustRed23.jdautils.music.impl.lavalink;

import dev.JustRed23.jdautils.music.PlayableTrack;
import dev.JustRed23.jdautils.music.PlaybackState;
import dev.JustRed23.jdautils.music.event.QueueUpdateEvent;
import dev.JustRed23.jdautils.music.event.TrackErrorEvent;
import dev.JustRed23.jdautils.music.event.TrackNotFoundEvent;
import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler;
import dev.arbjerg.lavalink.client.player.LoadFailed;
import dev.arbjerg.lavalink.client.player.PlaylistLoaded;
import dev.arbjerg.lavalink.client.player.SearchResult;
import dev.arbjerg.lavalink.client.player.TrackLoaded;
import net.dv8tion.jda.api.entities.Member;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static dev.JustRed23.jdautils.music.impl.lavalink.LavalinkUtils.fromLavalinkException;
import static dev.JustRed23.jdautils.music.impl.lavalink.LavalinkUtils.fromTrack;

public class LavalinkAudioLoadResultHandler extends AbstractAudioLoadResultHandler {

    private final LavalinkGuildMusicManager manager;
    private final LavalinkQueue queue;
    private final PlaybackState prevState;
    private final String url;
    private final Member member;

    public LavalinkAudioLoadResultHandler(LavalinkGuildMusicManager manager, PlaybackState prevState, String url, Member member) {
        this.manager = manager;
        queue = (LavalinkQueue) manager.queue();
        this.prevState = prevState;
        this.url = url;
        this.member = member;
    }

    public void ontrackLoaded(@NonNull TrackLoaded trackLoaded) {
        PlayableTrack track = fromTrack(trackLoaded.getTrack(), member);
        queue.addToQueue(track);
        manager.postEvent(new QueueUpdateEvent(manager.guild().getJDA(), manager.guild(), QueueUpdateEvent.QueueUpdateType.ADDED, List.of(track), queue.getQueue().size()));
    }

    public void onPlaylistLoaded(@NonNull PlaylistLoaded playlistLoaded) {
        List<PlayableTrack> tracks = playlistLoaded.getTracks().stream().map(t -> fromTrack(t, member)).toList();
        queue.addAllToQueue(tracks);
        manager.postEvent(new QueueUpdateEvent(manager.guild().getJDA(), manager.guild(), QueueUpdateEvent.QueueUpdateType.ADDED_PLAYLIST, tracks, queue.getQueue().size()));
    }

    public void onSearchResultLoaded(@NonNull SearchResult searchResult) {
        List<PlayableTrack> tracks = searchResult.getTracks().stream().map(t -> fromTrack(t, member)).toList();
        if (!tracks.isEmpty()) {
            queue.addToQueue(tracks.get(0));
            manager.postEvent(new QueueUpdateEvent(manager.guild().getJDA(), manager.guild(), QueueUpdateEvent.QueueUpdateType.ADDED, List.of(tracks.get(0)), queue.getQueue().size()));
        }
    }

    public void noMatches() {
        manager.postEvent(new TrackNotFoundEvent(manager.guild().getJDA(), manager.guild(), url));
        manager.setState(prevState);
    }

    public void loadFailed(@NonNull LoadFailed loadFailed) {
        manager.postEvent(new TrackErrorEvent(manager.guild().getJDA(), manager.guild(), null, fromLavalinkException(loadFailed.getException())));
        manager.setState(prevState);
    }
}
