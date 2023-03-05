package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.Nullable;

public final class TrackControls {

    private final TrackScheduler scheduler;
    private final AudioModifier modifier;

    TrackControls(TrackScheduler scheduler, AudioModifier modifier) {
        this.scheduler = scheduler;
        this.modifier = modifier;
    }

    void shutdown() {
        scheduler.shutdown();
    }

    public void pause() {
        scheduler.player.setPaused(true);
    }

    public void resume() {
        scheduler.player.setPaused(false);
    }

    public void restart() {
        if (scheduler.getPlayingTrack() != null)
            scheduler.player.getPlayingTrack().setPosition(0);
    }

    @Nullable
    public TrackInfo skip() {
        scheduler.looping = false;

        if (scheduler.isPaused())
            resume();

        if (scheduler.queue.isEmpty()) {
            stop();
            return null;
        }

        scheduler.player.startTrack(scheduler.queue.poll(), false);
        return scheduler.getPlayingTrackInfo();
    }

    @Nullable
    public TrackInfo prev() {
        if (scheduler.prev.isEmpty())
            return null;

        scheduler.player.startTrack(scheduler.prev.poll(), false);
        return scheduler.getPlayingTrackInfo();
    }

    public void stop() {
        scheduler.player.stopTrack();
        if (scheduler.isPaused())
            resume();
        modifier.disableEffect();
    }

    public void stopAndClear() {
        scheduler.queue.clear();
        scheduler.prev.clear();
        scheduler.looping = false;
        stop();
    }
}
