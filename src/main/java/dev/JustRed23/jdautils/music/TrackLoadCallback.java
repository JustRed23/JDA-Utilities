package dev.JustRed23.jdautils.music;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;

import java.util.List;

public interface TrackLoadCallback {
        void onTrackLoaded(TrackInfo trackInfo, boolean addedToQueue);
        void onPlaylistLoaded(AudioPlaylist playlist, List<TrackInfo> tracks, long totalDurationMs);
        void onNoMatches();
        void onTrackLoadError(Exception exception);
}
