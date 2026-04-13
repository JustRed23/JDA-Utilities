package dev.JustRed23.jdautils.music;

import dev.JustRed23.jdautils.music.event.MusicEventListener;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public interface MusicManager {

    GuildMusicManager forGuild(Guild guild);
    GuildMusicManager forGuild(long guildId);

    void addEventListener(@NotNull MusicEventListener listener);
    void removeEventListener(@NotNull MusicEventListener listener);

    void destroy();
}
