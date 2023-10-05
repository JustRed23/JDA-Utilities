package dev.JustRed23.jdautils.event;

import dev.JustRed23.jdautils.component.Component;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public final class EventWatcher<E extends Event> {

    private final Component component;
    private final Class<E> eventClass;
    private final List<Function<E, Boolean>> conditions = new ArrayList<>();
    private Listener<E> listener;
    private long expireTime = -1;
    private boolean singleUse = false;

    public EventWatcher(Component component, Class<E> eventClass) {
        this.component = component;
        this.eventClass = eventClass;
    }

    public EventWatcher(Component component, Class<E> eventClass, boolean singleUse) {
        this(component, eventClass);
        this.singleUse = singleUse;
    }

    public EventWatcher<E> setListener(Listener<E> listener) {
        this.listener = listener;
        WatcherManager.addWatcher(this);
        return this;
    }

    public EventWatcher<E> setListener(Listener<E> listener, int expireAfter, @NotNull TimeUnit unit) {
        this.expireTime = System.currentTimeMillis() + unit.toMillis(expireAfter);
        this.setListener(listener);
        return this;
    }

    public EventWatcher<E> addCondition(@NotNull Function<E, Boolean> condition) {
        Checks.notNull(condition, "Condition");
        Checks.notNull(eventClass, "Event class");
        conditions.add(condition);
        return this;
    }

    public EventWatcher<E> addConditions(@NotNull List<Function<E, Boolean>> conditions) {
        Checks.notNull(conditions, "Conditions");
        conditions.forEach(this::addCondition);
        return this;
    }

    public Listener<E> getListener() {
        return listener;
    }

    public void destroy() {
        listener = null;
        expireTime = -1;
        WatcherManager.removeWatcher(this);
    }

    void onEvent(Event rawEvent) {
        if (expired()) {
            destroy();
            return;
        }

        if (listener != null && rawEvent.getClass().equals(eventClass)) {
            E event = (E) rawEvent;

            boolean conditionsMet = conditions.stream().allMatch(condition -> condition.apply(event));
            if (!conditionsMet) return;

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

    public interface Listener<E> {
        void onEvent(E event);
    }
}
