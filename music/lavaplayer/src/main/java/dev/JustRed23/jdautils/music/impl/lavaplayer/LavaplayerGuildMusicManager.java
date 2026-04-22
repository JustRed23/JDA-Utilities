package dev.JustRed23.jdautils.music.impl.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.JustRed23.jdautils.music.*;
import dev.JustRed23.jdautils.music.event.*;
import dev.JustRed23.jdautils.music.exception.PlayerException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static dev.JustRed23.jdautils.music.impl.lavaplayer.LavaplayerUtils.fromTrack;

public class LavaplayerGuildMusicManager implements GuildMusicManager {

    private static final int MAX_CONSECUTIVE_FAILURES = 3;

    private final @NotNull Guild guild;
    private final @NotNull AudioPlayerManager client;
    private final @NotNull AudioPlayer player;
    private final @NotNull EventBus eventBus;

    private final LavaplayerQueue queue = new LavaplayerQueue(this);
    private final GuildPlayerOptions playerOptions = new LavaplayerPlayerOptions(this);
    private final LavaplayerInternalGuildEventListener listener = new LavaplayerInternalGuildEventListener(this);

    private volatile PlayableTrack currentTrack;
    private volatile PlaybackState currentState = PlaybackState.IDLE;
    private volatile int consecutiveFailures = 0;

    public LavaplayerGuildMusicManager(@NotNull Guild guild, @NotNull AudioPlayerManager client, @NotNull EventBus eventBus) {
        this.guild = guild;
        this.client = client;
        this.player = client.createPlayer();
        this.eventBus = eventBus;
        this.eventBus.addListener(listener);
        setupPlayer();
    }

    private void setupPlayer() {
        this.player.addListener(audioEvent -> {
            if (audioEvent instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent event) {
                eventBus.post(new TrackStartEvent(guild.getJDA(), guild, fromTrack(event.track)));
            } else if (audioEvent instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent event) {
                eventBus.post(new TrackEndEvent(guild.getJDA(), guild, fromTrack(event.track), event.endReason.mayStartNext));
            } else if (audioEvent instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackStuckEvent event) {
                eventBus.post(new TrackErrorEvent(guild.getJDA(), guild, fromTrack(event.track), new PlayerException("Track got stuck for more than " + event.thresholdMs + "ms")));
            } else if (audioEvent instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent event) {
                eventBus.post(new TrackErrorEvent(guild.getJDA(), guild, fromTrack(event.track), event.exception));
            }
        });

        guild.getAudioManager().setSendingHandler(new LavaplayerStreamHandler(player));
    }

    public @NotNull Guild guild() {
        return guild;
    }

    public void join(@NotNull AudioChannel channel) {
        AudioChannel current = getCurrentChannel();
        if (current == null || current.getIdLong() != channel.getIdLong()) {
            guild.getAudioManager().openAudioConnection(channel);
        }
    }

    public @Nullable AudioChannel getCurrentChannel() {
        var state = guild.getSelfMember().getVoiceState();
        return state != null ? state.getChannel() : null;
    }

    public void play(@NotNull String url, @NotNull AudioChannel channel) {
        join(channel);
        startTrack(url);
    }

    public void pause() {
        setPaused(true);
    }

    public void resume() {
        setPaused(false);
    }

    public void stop() {
        player.stopTrack();

        setState(PlaybackState.IDLE);
        setTrack(null);
        queue().clear();
    }

    public void disconnect() {
        stop();
        guild.getAudioManager().closeAudioConnection();
    }

    @Override
    public void seek(long positionMillis) {
        if (positionMillis < 0) {
            throw new IllegalArgumentException("Seek position cannot be negative");
        }

        PlayableTrack track = getCurrentTrack().orElseThrow(() -> new IllegalStateException("No track is currently playing"));
        AudioTrack raw = (AudioTrack) track.raw();

        if (raw == null) {
            throw new IllegalArgumentException("Track has no info");
        }

        if (!raw.isSeekable() || !track.hasDuration()) {
            throw new UnsupportedOperationException("This track does not support seeking");
        }

        if (positionMillis >= track.durationMillis()) {
            throw new IllegalArgumentException("Seek position cannot be at or beyond track duration");
        }

        player.getPlayingTrack().setPosition(positionMillis);
    }

    public void destroy() {
        disconnect();
        this.eventBus.removeListener(listener);
        guild.getAudioManager().setSendingHandler(null);
    }

    public @NotNull GuildQueueOptions queue() {
        return queue;
    }

    public @NotNull Optional<PlayableTrack> getCurrentTrack() {
        return Optional.ofNullable(currentTrack);
    }

    public long getTrackPosition() {
        return currentTrack != null ? player.getPlayingTrack().getPosition() : -1;
    }

    public @NotNull GuildPlayerOptions options() {
        return playerOptions;
    }

    public @NotNull PlaybackState getPlaybackState() {
        return currentState;
    }

    private void setPaused(boolean paused) {
        player.setPaused(paused);
        if (currentTrack == null) setState(PlaybackState.IDLE);
        else if (paused) setState(PlaybackState.PAUSED);
        else setState(PlaybackState.PLAYING);
    }

    @ApiStatus.Internal
    void postEvent(MusicEvent event) {
        eventBus.post(event);
    }

    @ApiStatus.Internal
    void setState(PlaybackState state) {
        postEvent(new PlaybackStateChangeEvent(guild.getJDA(), guild, currentState, state));
        this.currentState = state;
    }

    @ApiStatus.Internal
    void nextTrack() {
        queue.nextTrack();
    }

    @ApiStatus.Internal
    void setTrack(PlayableTrack track) {
        this.currentTrack = track;
    }

    @ApiStatus.Internal
    void startTrack(@NotNull String url) {
        var prevState = getPlaybackState();
        setState(PlaybackState.LOADING);
        client.loadItem(url, new LavaplayerAudioLoadResultHandler(this, prevState, url));
    }

    @ApiStatus.Internal
    void incrementConsecutiveFailures() {
        this.consecutiveFailures++;
    }

    @ApiStatus.Internal
    void resetConsecutiveFailures() {
        this.consecutiveFailures = 0;
    }

    @ApiStatus.Internal
    boolean exceedsFailureThreshold() {
        return consecutiveFailures >= MAX_CONSECUTIVE_FAILURES;
    }

    @ApiStatus.Internal
    @NotNull
    AudioPlayer getPlayer() {
        return player;
    }
}
