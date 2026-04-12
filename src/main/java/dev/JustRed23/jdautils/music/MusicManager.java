package dev.JustRed23.jdautils.music;

import net.dv8tion.jda.api.entities.Guild;

public interface MusicManager {

    GuildMusicManager forGuild(Guild guild);
    GuildMusicManager forGuild(long guildId);

    void destroy();
}
