package dev.JustRed23.jdautils.music.event;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

/**
 * An event bus for dispatching music events to registered listeners.
 * <p>
 * Provides efficient event dispatching without reflection. Listeners can be added or removed at runtime.
 * Exceptions thrown by listeners are logged without disrupting event dispatch.
 * <p>
 * Supports the following default event types:
 * <ul>
 *     <li>{@link TrackStartEvent}</li>
 *     <li>{@link TrackEndEvent}</li>
 *     <li>{@link TrackErrorEvent}</li>
 *     <li>{@link TrackNotFoundEvent}</li>
 *     <li>{@link PlaybackStateChangeEvent}</li>
 *     <li>{@link QueueUpdateEvent}</li>
 *     <li>{@link VolumeChangeEvent}</li>
 * </ul>
 * <p>
 * Custom events extending {@link MusicEvent} will be dispatched to {@link MusicEventListener#onCustomEvent}
 * unless a specific handler is registered.
 */
public class EventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    private static final BiConsumer<MusicEventListener, MusicEvent> DEFAULT_HANDLER = MusicEventListener::onCustomEvent;

    private final List<MusicEventListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<Class<? extends MusicEvent>, BiConsumer<MusicEventListener, MusicEvent>> dispatchMap = new HashMap<>();

    /**
     * Initializes the event bus and registers default handlers for common music events.
     */
    public EventBus() {
        register(TrackStartEvent.class, MusicEventListener::onTrackStart);
        register(TrackEndEvent.class, MusicEventListener::onTrackEnd);
        register(TrackErrorEvent.class, MusicEventListener::onTrackError);
        register(TrackNotFoundEvent.class, MusicEventListener::onTrackNotFound);
        register(PlaybackStateChangeEvent.class, MusicEventListener::onPlaybackStateChange);
        register(QueueUpdateEvent.class, MusicEventListener::onQueueUpdate);
        register(VolumeChangeEvent.class, MusicEventListener::onVolumeChange);
    }

    /**
     * Registers a new listener to receive music events.
     *
     * @param listener The listener to register.
     */
    public void addListener(@NotNull MusicEventListener listener) {
        getListeners().add(listener);
    }

    /**
     * Unregisters a listener from receiving music events.
     *
     * @param listener The listener to remove.
     */
    public void removeListener(@NotNull MusicEventListener listener) {
        getListeners().remove(listener);
    }

    /**
     * Gets the thread-safe list of registered listeners.
     * <p>
     * Can be overridden by subclasses to provide custom listener management.
     *
     * @return The list of listeners.
     */
    protected List<MusicEventListener> getListeners() {
        return listeners;
    }

    /**
     * Registers a handler for a specific music event type.
     * <p>
     * When an event of the specified type is posted, the handler will be invoked for each registered listener.
     *
     * @param type The event class to handle.
     * @param handler The handler that will be invoked for each listener.
     * @param <T> The type of music event.
     */
    protected final <T extends MusicEvent> void register(@NotNull Class<T> type, @NotNull BiConsumer<MusicEventListener, T> handler) {
        dispatchMap.put(type, (listener, event) -> handler.accept(listener, type.cast(event)));
    }

    /**
     * Posts an event to all registered listeners.
     * <p>
     * The event is dispatched to the appropriate handler based on its type.
     * If no specific handler is registered, it will be dispatched to {@link MusicEventListener#onCustomEvent}.
     * Any exceptions thrown by listeners are logged without interrupting dispatch.
     *
     * @param event The event to post.
     */
    public final void post(@NotNull MusicEvent event) {
        var handler = dispatchMap.get(event.getClass());
        if (handler == null) handler = DEFAULT_HANDLER;

        for (MusicEventListener listener : getListeners()) {
            try {
                handler.accept(listener, event);
            } catch (Exception e) {
                LOGGER.error("Error dispatching event {} to listener {}",
                        event.getClass().getSimpleName(),
                        listener.getClass().getSimpleName(),
                        e
                );
            }
        }
    }
}
