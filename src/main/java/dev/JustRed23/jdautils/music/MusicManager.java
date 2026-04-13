package dev.JustRed23.jdautils.music;

import dev.JustRed23.jdautils.music.event.MusicEventListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MusicManager {

    GuildMusicManager forGuild(Guild guild);
    GuildMusicManager forGuild(long guildId);

    void addEventListener(@NotNull MusicEventListener listener);
    void removeEventListener(@NotNull MusicEventListener listener);

    /**
     * Returns the voice dispatch interceptor used by this music manager, if any.
     * <p>Implementations that do not need one can return {@code null}.</p>
     */
    default @Nullable VoiceDispatchInterceptor getVoiceDispatchInterceptor() {
        return null;
    }

    void destroy();
}
