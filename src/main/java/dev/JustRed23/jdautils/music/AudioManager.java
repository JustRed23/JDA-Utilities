package dev.JustRed23.jdautils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.JustRed23.jdautils.music.effect.AbstractEffect;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AudioManager {

    private static final Map<Long, AudioManager> managers = new ConcurrentHashMap<>();
    private static boolean initialized = false;

    public static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    /**
     * Whether to register the default remote sources, disable this if you want to use your own sources
     * <br> Default: true
     * <br> Use {@link #playerManager} to register your own sources
     * @see AudioSourceManagers#registerRemoteSources(AudioPlayerManager)
     */
    public static boolean registerDefaultRemoteSources = true;

    private static void initPlayerManager() {
        if (initialized) return;

        if (registerDefaultRemoteSources) AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        initialized = true;
    }

    /**
     * Gets or creates the audio manager for the specified guild
     * @param guild The guild to get the audio manager for, must not be null
     * @return The audio manager for the specified guild
     */
    public static AudioManager get(@NotNull Guild guild) {
        Checks.notNull(guild, "Guild");
        return managers.computeIfAbsent(guild.getIdLong(), guildID -> new AudioManager(playerManager.createPlayer(), guild));
    }

    /**
     * Checks if the specified guild has an active audio manager
     * @param guild The guild to check, must not be null
     * @return True if the guild has an active audio manager, false otherwise
     */
    public static boolean has(@NotNull Guild guild) {
        Checks.notNull(guild, "Guild");
        return managers.containsKey(guild.getIdLong());
    }

    /**
     * Destroys all active audio managers
     */
    public static void destroyAll() {
        managers.values().forEach(AudioManager::destroy);
        managers.clear();
    }

    private final Guild guild;

    private TrackScheduler scheduler;
    private AudioModifier audioModifier;
    private TrackControls controls;

    private TextChannel boundChannel;

    private AudioManager(@NotNull AudioPlayer player, @NotNull Guild guild) {
        initPlayerManager();
        this.guild = guild;

        this.scheduler = new TrackScheduler(player, guild);
        this.audioModifier = new AudioModifier(player, guild);
        this.controls = new TrackControls(scheduler, audioModifier);

        guild.getAudioManager().setSendingHandler(new JDASendHandler(player));
    }

    /**
     * Binds the audio manager to the specified text channel, this essentially does nothing but can be useful for blocking access in other channels
     * <br><b>Note - The bound channel will automatically be reset if {@link #disconnect()} is called</b>
     * @param channel The text channel to bind to, must not be null
     * @see #isBoundChannel(TextChannel)
     */
    public void bindTextChannel(@NotNull TextChannel channel) {
        boundChannel = channel;
    }

    /**
     * Makes the bot join the specified voice channel
     * @param channel The voice channel to join, must not be null
     */
    public void join(@NotNull VoiceChannel channel) {
        guild.getAudioManager().openAudioConnection(channel);
    }

    /**
     * Makes the bot leave the voice channel
     */
    public void disconnect() {
        if (controls == null)
            throw new IllegalStateException("Audio manager has been destroyed");

        getControls().stopAndClear();
        getAudioModifier().disableEffect();

        if (isConnected())
            scheduler.getGuild().getAudioManager().closeAudioConnection();

        boundChannel = null;
    }

    /**
     * Loads and plays the specified track or playlist
     * @param trackURL The url of the track or playlist to load
     * @param requester The member that requested the track
     * @param callback The callback to call when the track or playlist has been loaded
     * @see TrackLoadCallback
     */
    public void loadAndPlay(@NotNull String trackURL, @NotNull Member requester, @NotNull TrackLoadCallback callback) {
        loadAndPlay(trackURL, requester.getUser(), callback);
    }

    /**
     * Loads and plays the specified track or playlist
     * @param trackURL The url of the track or playlist to load
     * @param requester The user that requested the track
     * @param callback The callback to call when the track or playlist has been loaded
     * @see TrackLoadCallback
     */
    public void loadAndPlay(@NotNull String trackURL, @NotNull User requester, @NotNull TrackLoadCallback callback) {
        Checks.notNull(trackURL, "Track url");
        Checks.notNull(requester, "Requester");
        Checks.notNull(callback, "Callback");

        //Strip <>'s that prevent discord from embedding link resources
        if (trackURL.startsWith("<") && trackURL.endsWith(">"))
            trackURL = trackURL.substring(1, trackURL.length() - 1);

        playerManager.loadItemOrdered(this, trackURL, new AudioLoadResultHandler() {

            public void trackLoaded(AudioTrack track) {
                boolean queued = scheduler.queue(track, requester);
                callback.onTrackLoaded(TrackInfo.of(getGuild(), track), queued, track.getDuration());
            }

            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                    return;
                }

                playlist.getTracks().forEach(track -> scheduler.queue(track, requester));
                List<TrackInfo> trackInfo = playlist.getTracks().stream().map(track -> TrackInfo.of(getGuild(), track)).toList();
                callback.onPlaylistLoaded(playlist, trackInfo, trackInfo.stream().map(TrackInfo::track).mapToLong(AudioTrack::getDuration).sum());
            }

            public void noMatches() {
                callback.onNoMatches();
            }

            public void loadFailed(FriendlyException exception) {
                callback.onTrackLoadError(exception);
            }
        });
    }

    /**
     * Destroys the audio manager and disconnects the bot from the voice channel
     */
    public void destroy() {
        if (controls == null)
            return;

        disconnect();
        controls.shutdown();

        controls = null;
        audioModifier = null;
        scheduler = null;

        guild.getAudioManager().setSendingHandler(null);
        managers.remove(guild.getIdLong());
    }

    /**
     * Gets the track scheduler, which handles the queue
     * @return The track scheduler
     */
    public TrackScheduler getScheduler() {
        if (scheduler == null)
            throw new IllegalStateException("AudioManager has been destroyed");
        return scheduler;
    }

    /**
     * Gets the track controls, which handles pausing, resuming, skipping, etc.
     * @return The track controls
     */
    public TrackControls getControls() {
        if (controls == null)
            throw new IllegalStateException("AudioManager has been destroyed");
        return controls;
    }

    /**
     * Gets the audio modifier, here you can change the volume, pitch, etc.
     * <br> You can also add your own effects!
     * @see AbstractEffect
     * @return The audio modifier
     */
    public AudioModifier getAudioModifier() {
        if (audioModifier == null)
            throw new IllegalStateException("AudioManager has been destroyed");
        return audioModifier;
    }

    public AudioChannelUnion getConnectedChannel() {
        return guild.getAudioManager().getConnectedChannel();
    }

    public boolean isConnected() {
        return getConnectedChannel() != null;
    }

    public Guild getGuild() {
        return guild;
    }

    public TextChannel getBoundChannel() {
        return boundChannel;
    }

    public boolean isBoundChannel(TextChannel channel) {
        return boundChannel != null && boundChannel.equals(channel);
    }
}
