package dev.JustRed23.jdautils.music;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages scheduling of auto-disconnect tasks for guild music managers.
 */
public final class AutoDisconnectManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDisconnectManager.class);

    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(0);
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(4, r -> {
        Thread t = new Thread(r, "jdautils-music-auto-disconnect-" + THREAD_COUNTER.incrementAndGet());
        t.setDaemon(true);
        return t;
    });

    private static final Map<Long, ScheduledFuture<?>> TASKS = new ConcurrentHashMap<>();

    private AutoDisconnectManager() { }

    public static void schedule(final GuildMusicManager manager, long delaySeconds) {
        Objects.requireNonNull(manager, "manager");
        if (delaySeconds <= 0) return;
        long guildId = manager.guild().getIdLong();
        ScheduledFuture<?> future = SCHEDULER.schedule(() -> {
            try {
                if (manager.getPlaybackState() == PlaybackState.IDLE && manager.options().isAutoDisconnect())
                    manager.disconnect();
            } catch (Throwable t) {
                LOGGER.error("Error during auto-disconnect for guild {}", guildId, t);
            } finally {
                TASKS.remove(guildId);
            }
        }, delaySeconds, TimeUnit.SECONDS);
        ScheduledFuture<?> old = TASKS.put(guildId, future);
        if (old != null && !old.isDone()) old.cancel(false);
    }

    public static void cancel(final GuildMusicManager manager) {
        Objects.requireNonNull(manager, "manager");
        ScheduledFuture<?> f = TASKS.remove(manager.guild().getIdLong());
        if (f != null && !f.isDone()) f.cancel(false);
    }
}
