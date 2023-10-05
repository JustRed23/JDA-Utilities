package dev.JustRed23.jdautils.message;

import dev.JustRed23.jdautils.event.EventWatcher;
import dev.JustRed23.jdautils.event.custom.MessageFilterEvent;
import dev.JustRed23.jdautils.utils.Unique;
import org.jetbrains.annotations.NotNull;

public abstract class Filter {

    private final String name;
    private final String description;
    private final EventWatcher<MessageFilterEvent> watcher;

    protected Filter(String name, String description) {
        Unique.checkUnique("filter-name", name, "Filter name is not unique");
        this.name = name;
        this.description = description;

        watcher = new EventWatcher<>(new MessageComponent(this), MessageFilterEvent.class);
    }

    /**
     * Sets the listener for the filter, must be called if you want to set a custom action
     * <br>If the listener is not set, the filter will delete the message that triggered it.
     * @param listener The listener
     */
    public final Filter withListener(@NotNull EventWatcher.Listener<MessageFilterEvent> listener) {
        watcher.setListener(listener);
        return this;
    }

    /**
     * Called when a message is received
     * @param event The event
     * @return True if the filter triggered
     */
    protected abstract boolean onMessageReceived(@NotNull MessageFilterEvent event);

    protected final void destroy() {
        watcher.destroy();
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }
}
