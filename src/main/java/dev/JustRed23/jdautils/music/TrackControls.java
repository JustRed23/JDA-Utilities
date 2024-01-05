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
        scheduler.getPlayer().setPaused(true);
    }

    public void resume() {
        scheduler.getPlayer().setPaused(false);
    }

    public void restart() {
        if (scheduler.getPlayingTrack() != null)
            scheduler.getPlayingTrack().setPosition(0);
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

        scheduler.getPlayer().startTrack(scheduler.queue.poll(), false);
        return scheduler.getPlayingTrackInfo();
    }

    @Nullable
    public TrackInfo prev() {
        if (scheduler.prev.isEmpty())
            return null;

        scheduler.getPlayer().startTrack(scheduler.prev.poll(), false);
        return scheduler.getPlayingTrackInfo();
    }

    public void stop() {
        scheduler.getPlayer().stopTrack();
        if (scheduler.isPaused())
            resume();
        modifier.disableEffect();
    }

    public void stopAndClear() {
        scheduler.queue.clear();
        scheduler.prev.clear();
        scheduler.looping = false;
        scheduler.manualStateChange = false;
        scheduler.currentStatus = null;
        scheduler.setChannelStatus(null);
        stop();
    }
}
