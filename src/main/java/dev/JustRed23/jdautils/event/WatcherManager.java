package dev.JustRed23.jdautils.event;

import dev.JustRed23.jdautils.command.CommandComponent;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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

    public static void onCommandEvent(SlashCommandInteractionEvent event) {
        String command = event.getName() + (event.getSubcommandName() != null ? " " + event.getSubcommandName() : "");

        watchers.stream()
                .filter(watcher -> watcher.getComponent() instanceof CommandComponent)
                .filter(watcher -> watcher.getComponent().getName().equals(command))
                .findFirst()
                .ifPresent(watcher -> watcher.onEvent(event));
    }

    public static void onEvent(String componentID, Event event) {
        watchers.stream()
                .filter(watcher -> watcher.getComponent().getUuid() != null)
                .filter(watcher -> watcher.getComponent().getUuid().toString().equals(componentID))
                .findFirst()
                .ifPresent(watcher -> watcher.onEvent(event));
    }
}