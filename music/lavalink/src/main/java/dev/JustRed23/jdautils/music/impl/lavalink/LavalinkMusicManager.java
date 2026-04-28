package dev.JustRed23.jdautils.music.impl.lavalink;

import dev.JustRed23.jdautils.music.GuildMusicManager;
import dev.JustRed23.jdautils.music.MusicManager;
import dev.JustRed23.jdautils.music.event.*;
import dev.JustRed23.jdautils.music.exception.PlayerException;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import dev.arbjerg.lavalink.protocol.v4.Message;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dev.JustRed23.jdautils.music.impl.lavalink.LavalinkUtils.fromLavalinkException;
import static dev.JustRed23.jdautils.music.impl.lavalink.LavalinkUtils.fromTrack;

public final class LavalinkMusicManager implements MusicManager {

    private final Map<Long, GuildMusicManager> managers = new ConcurrentHashMap<>();
    private final LavalinkClient client;
    private final JDAVoiceUpdateListener listener;
    private final EventBus eventBus;

    public LavalinkMusicManager(@NotNull LavalinkClient client) {
        this.client = client;
        this.listener = new JDAVoiceUpdateListener(client);
        this.eventBus = new EventBus();
        setupClientEvents();
    }

    public GuildMusicManager forGuild(@NotNull Guild guild) {
        return managers.computeIfAbsent(guild.getIdLong(), id -> new LavalinkGuildMusicManager(guild, client, eventBus));
    }

    public void addEventListener(@NotNull MusicEventListener listener) {
        eventBus.addListener(listener);
    }

    public void removeEventListener(@NotNull MusicEventListener listener) {
        eventBus.removeListener(listener);
    }

    public @NotNull VoiceDispatchInterceptor getVoiceDispatchInterceptor() {
        return listener;
    }

    public void destroy() {
        managers.forEach((id, manager) -> manager.destroy());
        managers.clear();

        client.dispose();
    }

    //<editor-fold desc="Lavalink Client Events">
    private void setupClientEvents() {
        client.on(dev.arbjerg.lavalink.client.event.TrackStartEvent.class).subscribe(event -> {
            final GuildMusicManager guildMusicManager = managers.get(event.getGuildId());
            if (guildMusicManager == null) return;
            Guild guild = guildMusicManager.guild();

            eventBus.post(new TrackStartEvent(guild.getJDA(), guild, fromTrack(event.getTrack(), getMember(guildMusicManager))));
        });

        client.on(dev.arbjerg.lavalink.client.event.TrackEndEvent.class).subscribe(event -> {
            final GuildMusicManager guildMusicManager = managers.get(event.getGuildId());
            if (guildMusicManager == null) return;
            Guild guild = guildMusicManager.guild();

            eventBus.post(new TrackEndEvent(guild.getJDA(), guild, fromTrack(event.getTrack(), getMember(guildMusicManager)), event.getEndReason().getMayStartNext(), event.getEndReason() == Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason.REPLACED));
        });

        client.on(dev.arbjerg.lavalink.client.event.TrackStuckEvent.class).subscribe(event -> {
            final GuildMusicManager guildMusicManager = managers.get(event.getGuildId());
            if (guildMusicManager == null) return;
            Guild guild = guildMusicManager.guild();

            eventBus.post(new TrackErrorEvent(guild.getJDA(), guild, fromTrack(event.getTrack(), getMember(guildMusicManager)), new PlayerException("Track got stuck for more than " + event.getThresholdMs() + "ms")));
        });

        client.on(dev.arbjerg.lavalink.client.event.TrackExceptionEvent.class).subscribe(event -> {
            final GuildMusicManager guildMusicManager = managers.get(event.getGuildId());
            if (guildMusicManager == null) return;
            Guild guild = guildMusicManager.guild();

            eventBus.post(new TrackErrorEvent(guild.getJDA(), guild, fromTrack(event.getTrack(), getMember(guildMusicManager)), fromLavalinkException(event.getException())));
        });

        client.on(dev.arbjerg.lavalink.client.event.PlayerUpdateEvent.class).subscribe(event -> {
            final GuildMusicManager guildMusicManager = managers.get(event.getGuildId());
            if (guildMusicManager == null) return;
            ((LavalinkGuildMusicManager) guildMusicManager).setPosition(event.getState().getPosition());
        });
    }

    private Member getMember(GuildMusicManager manager) {
        return ((LavalinkGuildMusicManager) manager).getTrackMember();
    }
    //</editor-fold>
}
