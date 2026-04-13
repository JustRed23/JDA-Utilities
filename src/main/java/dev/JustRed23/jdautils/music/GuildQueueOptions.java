package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GuildQueueOptions {

    void skip();
    void back();
    void clear();
    void shuffle();

    @NotNull List<PlayableTrack> getQueue();
    @NotNull List<PlayableTrack> getHistory();

    default @Nullable PlayableTrack peek() {
        List<PlayableTrack> queue = getQueue();
        return queue.isEmpty() ? null : queue.get(0);
    }
}
