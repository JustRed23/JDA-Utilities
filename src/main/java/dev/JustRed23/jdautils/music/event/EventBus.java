package dev.JustRed23.jdautils.music.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class EventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    private static final BiConsumer<MusicEventListener, MusicEvent> DEFAULT_HANDLER = MusicEventListener::onCustomEvent;

    private final List<MusicEventListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<Class<? extends MusicEvent>, BiConsumer<MusicEventListener, MusicEvent>> dispatchMap = new HashMap<>();

    public EventBus() {
        register(TrackStartEvent.class, MusicEventListener::onTrackStart);
        register(TrackEndEvent.class, MusicEventListener::onTrackEnd);
        register(TrackErrorEvent.class, MusicEventListener::onTrackError);
        register(TrackNotFoundEvent.class, MusicEventListener::onTrackNotFound);
        register(PlaybackStateChangeEvent.class, MusicEventListener::onPlaybackStateChange);
        register(QueueUpdateEvent.class, MusicEventListener::onQueueUpdate);
        register(VolumeChangeEvent.class, MusicEventListener::onVolumeChange);
    }

    public void addListener(MusicEventListener listener) {
        getListeners().add(listener);
    }

    public void removeListener(MusicEventListener listener) {
        getListeners().remove(listener);
    }

    protected List<MusicEventListener> getListeners() {
        return listeners;
    }

    protected final <T extends MusicEvent> void register(Class<T> type, BiConsumer<MusicEventListener, T> handler) {
        dispatchMap.put(type, (listener, event) -> handler.accept(listener, type.cast(event)));
    }

    public final void post(MusicEvent event) {
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
