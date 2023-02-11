package dev.JustRed23.jdautils.event;

import net.dv8tion.jda.api.events.Event;

import java.util.ArrayList;
import java.util.List;

public final class WatcherManager {

    private static final List<EventWatcher> watchers = new ArrayList<>();

    private WatcherManager() {}

    static void addWatcher(EventWatcher watcher) {
        watchers.add(watcher);
    }

    static void removeWatcher(EventWatcher watcher) {
        watchers.remove(watcher);
    }

    public static void onEvent(String componentID, Event event) {
        watchers.stream()
                .filter(watcher -> watcher.getComponent().getUuid() != null)
                .filter(watcher -> watcher.getComponent().getUuid().toString().equals(componentID))
                .findFirst()
                .ifPresent(watcher -> watcher.onEvent(event));
    }
}
