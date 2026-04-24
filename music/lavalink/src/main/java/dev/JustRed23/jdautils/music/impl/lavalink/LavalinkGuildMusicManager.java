package dev.JustRed23.jdautils.music.impl.lavalink;

import dev.JustRed23.jdautils.music.*;
import dev.JustRed23.jdautils.music.event.EventBus;
import dev.JustRed23.jdautils.music.event.MusicEvent;
import dev.JustRed23.jdautils.music.event.PlaybackStateChangeEvent;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class LavalinkGuildMusicManager implements GuildMusicManager {

    private static final int MAX_CONSECUTIVE_FAILURES = 3;

    private final @NotNull Guild guild;
    private final @NotNull LavalinkClient client;
    private final @NotNull EventBus eventBus;

    private final LavalinkQueue queue = new LavalinkQueue(this);
    private final GuildPlayerOptions playerOptions = new LavalinkPlayerOptions(this);
    private final LavalinkInternalGuildEventListener listener = new LavalinkInternalGuildEventListener(this);

    private volatile PlayableTrack currentTrack;
    private volatile PlaybackState currentState = PlaybackState.IDLE;
    private volatile long currentPosition = 0;
    private volatile int consecutiveFailures = 0;
    private volatile TextChannel boundChannel;

    public LavalinkGuildMusicManager(@NotNull Guild guild, @NotNull LavalinkClient client, @NotNull EventBus eventBus) {
        this.guild = guild;
        this.client = client;
        this.eventBus = eventBus;
        this.eventBus.addListener(listener);
    }

    public @NotNull Guild guild() {
        return guild;
    }

    public void join(@NotNull AudioChannel channel) {
        AudioChannel current = getCurrentChannel();
        if (current == null || current.getIdLong() != channel.getIdLong()) {
            guild.getJDA().getDirectAudioController().connect(channel);
        }
    }

    public void bind(@NotNull TextChannel channel) {
        this.boundChannel = channel;
    }

    public @Nullable AudioChannel getCurrentChannel() {
        var state = guild.getSelfMember().getVoiceState();
        return state != null ? state.getChannel() : null;
    }

    public TextChannel getBoundChannel() {
        return boundChannel;
    }

    public void play(@NotNull String url, @NotNull AudioChannel channel, @NotNull Member member) {
        join(channel);
        startTrack(url, member);
    }

    public void pause() {
        setPaused(true);
    }

    public void resume() {
        setPaused(false);
    }

    public void stop() {
        getLink().getPlayer().subscribe(player ->
                player.setPaused(false)
                        .stopTrack()
                        .subscribe()
        );

        setState(PlaybackState.IDLE);
        setTrack(null);
        queue().clear();
    }

    public void disconnect() {
        stop();
        guild.getJDA().getDirectAudioController().disconnect(guild);
    }

    @Override
    public void seek(long positionMillis) {
        if (positionMillis < 0) {
            throw new IllegalArgumentException("Seek position cannot be negative");
        }

        PlayableTrack track = getCurrentTrack().orElseThrow(() -> new IllegalStateException("No track is currently playing"));
        Track raw = (Track) track.raw();

        if (raw == null) {
            throw new IllegalArgumentException("Track has no info");
        }

        if (!raw.getInfo().isSeekable() || !track.hasDuration()) {
            throw new UnsupportedOperationException("This track does not support seeking");
        }

        if (positionMillis >= track.durationMillis()) {
            throw new IllegalArgumentException("Seek position cannot be at or beyond track duration");
        }

        getLink().getPlayer().subscribe(player -> player.setPosition(positionMillis).subscribe());
    }

    public void destroy() {
        disconnect();
        this.eventBus.removeListener(listener);
    }

    public @NotNull GuildQueueOptions queue() {
        return queue;
    }

    public @NotNull Optional<PlayableTrack> getCurrentTrack() {
        return Optional.ofNullable(currentTrack);
    }

    public long getTrackPosition() {
        return currentTrack != null ? currentPosition : -1;
    }

    public @NotNull GuildPlayerOptions options() {
        return playerOptions;
    }

    public @NotNull PlaybackState getPlaybackState() {
        return currentState;
    }

    private void setPaused(boolean paused) {
        getLink().getPlayer().subscribe(player ->
                player.setPaused(paused).subscribe(updated -> {
                    if (currentTrack == null) setState(PlaybackState.IDLE);
                    else if (updated.getPaused()) setState(PlaybackState.PAUSED);
                    else setState(PlaybackState.PLAYING);
                })
        );
    }

    @ApiStatus.Internal
    @Nullable Member getTrackMember() {
        return getCurrentTrack().map(PlayableTrack::member).orElse(null);
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
    void setPosition(long position) {
        this.currentPosition = position;
    }

    @ApiStatus.Internal
    void startTrack(@NotNull String url, @NotNull Member member) {
        var prevState = getPlaybackState();
        setState(PlaybackState.LOADING);
        getLink().loadItem(url).subscribe(new LavalinkAudioLoadResultHandler(this, prevState, url, member));
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
    Link getLink() {
        return client.getOrCreateLink(guild.getIdLong());
    }
}
