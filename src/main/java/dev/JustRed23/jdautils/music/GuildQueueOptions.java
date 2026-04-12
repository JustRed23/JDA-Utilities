package dev.JustRed23.jdautils.music;

import java.util.List;

public interface GuildQueueOptions {

    void skip();
    void back();
    void clear();
    void shuffle();

    List<PlayableTrack> getQueue();
    List<PlayableTrack> getHistory();
}
