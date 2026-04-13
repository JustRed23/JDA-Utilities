package dev.JustRed23.jdautils.music.event;

import dev.JustRed23.jdautils.music.PlayableTrack;
import org.jetbrains.annotations.NotNull;

public record QueueUpdateEvent(
        long guildId,
        @NotNull QueueUpdateType type,
        @NotNull PlayableTrack track,
        int index
) implements MusicEvent {

    public enum QueueUpdateType {
        ADDED,
        REMOVED,
        MOVED,
        SHUFFLED,
        CLEARED
    }
}
