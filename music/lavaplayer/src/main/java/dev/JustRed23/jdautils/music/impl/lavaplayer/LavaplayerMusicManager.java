package dev.JustRed23.jdautils.music.impl.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import dev.JustRed23.jdautils.music.GuildMusicManager;
import dev.JustRed23.jdautils.music.MusicManager;
import dev.JustRed23.jdautils.music.event.*;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LavaplayerMusicManager implements MusicManager {

    private final Map<Long, GuildMusicManager> managers = new ConcurrentHashMap<>();
    private final AudioPlayerManager client;
    private final EventBus eventBus;

    public LavaplayerMusicManager(@NotNull AudioPlayerManager client) {
        this.client = client;
        this.eventBus = new EventBus();
    }

    public GuildMusicManager forGuild(@NotNull Guild guild) {
        return managers.computeIfAbsent(guild.getIdLong(), id -> new LavaplayerGuildMusicManager(guild, client, eventBus));
    }

    public void addEventListener(@NotNull MusicEventListener listener) {
        eventBus.addListener(listener);
    }

    public void removeEventListener(@NotNull MusicEventListener listener) {
        eventBus.removeListener(listener);
    }

    public void destroy() {
        managers.forEach((id, manager) -> manager.destroy());
        managers.clear();

        client.shutdown();
    }
}
