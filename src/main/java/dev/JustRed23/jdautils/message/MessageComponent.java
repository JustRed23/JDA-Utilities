package dev.JustRed23.jdautils.message;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.event.EventWatcher;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * <b>Internal use only</b><br>
 * Used to register message events to an {@link EventWatcher}
 */
public final class MessageComponent extends Component implements NoRegistry {

    private final Function<MessageReceivedEvent, Boolean> condition;
    private Filter filter;

    MessageComponent(Function<MessageReceivedEvent, Boolean> condition) {
        super("MessageComponent");
        this.condition = condition;
    }

    public MessageComponent(Filter filter) {
        super("MessageComponent");
        this.filter = filter;
        this.condition = event -> true;
    }

    public boolean conditionsMet(MessageReceivedEvent event) {
        return condition.apply(event);
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
