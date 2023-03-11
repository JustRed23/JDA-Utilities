package dev.JustRed23.jdautils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class TrackScheduler extends AudioEventAdapter {

    private final Guild guild;
    private AudioPlayer player;

    final LinkedList<AudioTrack>
            queue = new LinkedList<>(),
            prev = new LinkedList<>();

    private final List<AudioEventAdapter> listeners = new ArrayList<>();

    boolean looping = false;

    TrackScheduler(@NotNull AudioPlayer player, @NotNull Guild guild) {
        this.player = player;
        this.guild = guild;

        player.addListener(this);
    }

    void shutdown() {
        listeners.forEach(player::removeListener);
        player.removeListener(this);
        player.destroy();
        player = null;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, @NotNull AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (looping) {
                player.startTrack(track.makeClone(), false);
                return;
            }

            prev.add(track);
            if (!queue.isEmpty())
                player.startTrack(queue.poll(), false);
        }
    }

    /**
     * Queues a track to the player
     * @param track The track to queue
     * @param requester The user who requested the track
     * @return True if the track was queued, false otherwise
     */
    public boolean queue(@NotNull AudioTrack track, @NotNull User requester) {
        Checks.notNull(track, "Track");
        Checks.notNull(requester, "Requester");

        track.setUserData(requester);
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            return true;
        }
        return false;
    }

    public void swap(int track1Pos, int track2Pos) {
        Checks.check(track1Pos >= 0 && track1Pos < queue.size(), "Track 1 position is out of bounds");
        Checks.check(track2Pos >= 0 && track2Pos < queue.size(), "Track 2 position is out of bounds");

        Collections.swap(queue, track1Pos, track2Pos);
    }

    public void shuffle() {
        Collections.shuffle(queue);
    }

    public void addEventListener(@NotNull AudioEventAdapter listener) {
        Checks.notNull(listener, "Listener");

        listeners.add(listener);
        player.addListener(listener);
    }

    public @Nullable AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    public @Nullable TrackInfo getPlayingTrackInfo() {
        if (getPlayingTrack() == null)
            return null;
        return TrackInfo.of(getGuild(), getPlayingTrack());
    }

    public boolean isPlaying() {
        return player.getPlayingTrack() != null;
    }

    public boolean isLooping() {
        return looping;
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public LinkedList<AudioTrack> getQueue() {
        return queue;
    }

    public Guild getGuild() {
        return guild;
    }

    public AudioPlayer getPlayer() {
        return player;
    }
}
