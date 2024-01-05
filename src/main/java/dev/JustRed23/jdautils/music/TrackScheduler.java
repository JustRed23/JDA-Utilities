package dev.JustRed23.jdautils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.JustRed23.jdautils.JDAUtilities;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class TrackScheduler extends AudioEventAdapter {

    private final String
            PLAY = Emoji.fromUnicode("\u25B6\uFE0F").getFormatted() + " ",
            PAUSE = Emoji.fromUnicode("\u23F8\uFE0F").getFormatted() + " ";

    private final Guild guild;
    private AudioPlayer player;

    final LinkedList<AudioTrack>
            queue = new LinkedList<>(),
            prev = new LinkedList<>();

    private final List<AudioEventAdapter> listeners = new ArrayList<>();

    boolean looping = false;
    boolean showTrackInChannelStatus = true;
    boolean manualStateChange = false;
    public String currentStatus;

    TrackScheduler(@NotNull AudioPlayer player, @NotNull Guild guild) {
        this.player = player;
        this.guild = guild;

        player.addListener(this);

        if (JDAUtilities.getGuildSettingManager() != null)
            setShowTrackInChannelStatus(JDAUtilities.getGuildSettingManager().getOrDefault(guild.getIdLong(), "audioplayer-show-track-in-channel-status", true).booleanValue());
    }

    void shutdown() {
        setChannelStatus(null);
        listeners.forEach(player::removeListener);
        player.removeListener(this);
        player.destroy();
        player = null;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        String emoji = isPaused() ? PAUSE : PLAY;
        setChannelStatus(emoji + track.getInfo().title);
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        if (getPlayingTrack() != null)
            setChannelStatus(PAUSE + getPlayingTrack().getInfo().title);
        else setChannelStatus(null);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        if (getPlayingTrack() != null)
            setChannelStatus(PLAY + getPlayingTrack().getInfo().title);
        else setChannelStatus(null);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, @NotNull AudioTrackEndReason endReason) {
        setChannelStatus(null);
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

    void setChannelStatus(@Nullable String status) {
        if (!AudioManager.get(getGuild()).isConnected()) return;
        if (!showTrackInChannelStatus || manualStateChange) return;

        final AudioChannelUnion connectedChannel = guild.getAudioManager().getConnectedChannel();
        if (connectedChannel != null && connectedChannel.getType().isAudio()) {
            final VoiceChannel voiceChannel = connectedChannel.asVoiceChannel();

            String newStatus = status == null ? "" : status;
            if (voiceChannel.MAX_STATUS_LENGTH <= newStatus.length())
                newStatus = newStatus.substring(0, voiceChannel.MAX_STATUS_LENGTH - 4) + "...";

            currentStatus = newStatus;
            voiceChannel.modifyStatus(newStatus).queue();
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

    /**
     * Whether to show the current track in the channel status
     * @param showTrackInChannelStatus True if the track should be shown, false otherwise
     */
    public void setShowTrackInChannelStatus(boolean showTrackInChannelStatus) {
        this.showTrackInChannelStatus = showTrackInChannelStatus;
        if (JDAUtilities.getGuildSettingManager() != null)
            JDAUtilities.getGuildSettingManager().set(guild.getIdLong(), "audioplayer-show-track-in-channel-status", showTrackInChannelStatus);
    }

    /**
     * Indicates that a user has manually changed the channel status, we will now stop changing it to the next song until the player is stopped
     */
    @ApiStatus.Internal
    public void channelStatusChangedManually() {
        manualStateChange = true;
    }

    /**
     * Checks if the current channel status is the same as the given status
     * @param status The status to check
     * @return True if the channel status is the same as the given status, false otherwise
     */
    @ApiStatus.Internal
    public boolean isCurrentStatus(@Nullable String status) {
        if (currentStatus == null && status == null)
            return true;

        if (status == null || status.isEmpty() || !status.contains(" "))
            return false;

        return currentStatus != null && currentStatus.endsWith(status.substring(status.indexOf(" ")));
    }

    /**
     * Indicates that a user has manually changed the channel status
     * @return True if the channel status was changed manually, false otherwise
     */
    public boolean isStatusChangedManually() {
        return manualStateChange;
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
