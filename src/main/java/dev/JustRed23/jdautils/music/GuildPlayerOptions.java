package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;

public interface GuildPlayerOptions {

    void setVolume(float volume);
    float getVolume();

    void setAutoDisconnect(boolean autoDisconnect);
    boolean isAutoDisconnect();

    void updateUserStatusWithSongInfo(boolean enabled);
    boolean isUpdateUserStatusWithSongInfo();

    default void setRepeatMode(@NotNull RepeatMode repeatMode) {
        throw new UnsupportedOperationException("Repeat mode is not supported by this player");
    }

    default @NotNull RepeatMode getRepeatMode() {
        throw new UnsupportedOperationException("Repeat mode is not supported by this player");
    }
}
