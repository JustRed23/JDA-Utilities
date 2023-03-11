package dev.JustRed23.jdautils.event;

import dev.JustRed23.jdautils.component.Component;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class EventWatcher {

    private final Component component;
    private final Class<? extends Event> eventClass;
    private Listener listener;
    private long expireTime = -1;
    private boolean singleUse = false;

    public EventWatcher(Component component, Class<? extends Event> eventClass) {
        this.component = component;
        this.eventClass = eventClass;
    }

    public EventWatcher(Component component, Class<? extends Event> eventClass, boolean singleUse) {
        this(component, eventClass);
        this.singleUse = singleUse;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        WatcherManager.addWatcher(this);
    }

    public void setListener(Listener listener, int expireAfter, @NotNull TimeUnit unit) {
        this.expireTime = System.currentTimeMillis() + unit.toMillis(expireAfter);
        this.setListener(listener);
    }

    public Listener getListener() {
        return listener;
    }

    public void destroy() {
        listener = null;
        expireTime = -1;
        WatcherManager.removeWatcher(this);
    }

    void onEvent(Event event) {
        if (expired()) {
            destroy();
            return;
        }

        if (listener != null && event.getClass().equals(eventClass)) {
            listener.onEvent(event);
            if (singleUse)
                destroy();
        }
    }

    public Component getComponent() {
        return component;
    }

    public boolean expired() {
        return expireTime != -1 && System.currentTimeMillis() > expireTime;
    }

    public interface Listener<T extends Event> {
        void onEvent(T event);
    }
}
