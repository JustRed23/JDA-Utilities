package dev.JustRed23.jdautils.event;

import dev.JustRed23.jdautils.component.Component;
import net.dv8tion.jda.api.events.Event;

public class EventWatcher {

    private final Component component;
    private final Class<? extends Event> eventClass;
    private Listener listener;

    public EventWatcher(Component component, Class<? extends Event> eventClass) {
        this.component = component;
        this.eventClass = eventClass;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void destroy() {
        listener = null;
    }

    public void onEvent(Event event) {
        if (listener != null && event.getClass().equals(eventClass))
            listener.onEvent(event);
    }

    public Component getComponent() {
        return component;
    }

    public interface Listener<T extends Event> {
        void onEvent(T event);
    }
}
