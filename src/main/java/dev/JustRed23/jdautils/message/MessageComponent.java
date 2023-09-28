package dev.JustRed23.jdautils.message;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.event.EventWatcher;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * <b>Internal use only</b><br>
 * Used to register message events to an {@link EventWatcher}
 */
@ApiStatus.Internal
public final class MessageComponent extends Component implements NoRegistry {

    private EventWatcher.Listener listener;
    private Filter filter;
    private Function<MessageReceivedEvent, Boolean> condition = event -> true;

    public MessageComponent(EventWatcher.Listener listener) {
        super("MessageComponent");
        this.listener = listener;
    }

    public MessageComponent(Filter filter) {
        super("MessageComponent");
        this.filter = filter;
    }

    public MessageComponent withCondition(Function<MessageReceivedEvent, Boolean> condition) {
        this.condition = condition;
        return this;
    }

    public boolean conditionsMet(EventWatcher watcher, MessageReceivedEvent event) {
        if (isListener())
            return watcher.getListener().equals(listener) && condition.apply(event);
        return condition.apply(event);
    }

    public boolean isListener() {
        return listener != null;
    }

    public @Nullable EventWatcher.Listener getListener() {
        return listener;
    }

    public boolean isFilter() {
        return filter != null;
    }

    @Contract(pure = true)
    public @Nullable String getFilterName() {
        return isFilter() ? filter.getName() : null;
    }

    protected void onCreate() {}
    protected void onRemove() {}
}
