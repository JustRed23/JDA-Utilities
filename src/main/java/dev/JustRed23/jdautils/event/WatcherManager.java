package dev.JustRed23.jdautils.event;

import net.dv8tion.jda.api.events.Event;

import java.util.ArrayList;
import java.util.List;

public class WatcherManager {

    private static final List<EventWatcher> watchers = new ArrayList<>();

    private WatcherManager() {}

    public static void addWatcher(EventWatcher watcher) {
        watchers.add(watcher);
    }

    public static void onEvent(String componentID, Event event) {
        watchers.stream()
                .filter(watcher -> watcher.getComponent().getUuid() != null)
                .filter(watcher -> watcher.getComponent().getUuid().toString().equals(componentID))
                .findFirst()
                .ifPresent(watcher -> watcher.onEvent(event));
    }
}
