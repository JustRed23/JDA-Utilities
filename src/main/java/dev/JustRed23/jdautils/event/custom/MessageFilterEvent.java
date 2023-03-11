package dev.JustRed23.jdautils.event.custom;

import dev.JustRed23.jdautils.message.Filter;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MessageFilterEvent extends MessageReceivedEvent {

    private final Filter filter;

    private MessageFilterEvent(@NotNull Filter filter, @NotNull MessageReceivedEvent event) {
        super(event.getJDA(), event.getResponseNumber(), event.getMessage());
        this.filter = filter;
    }

    @Contract("_, _ -> new")
    public static @NotNull MessageFilterEvent of(@NotNull Filter filter, @NotNull MessageReceivedEvent event) {
        Checks.notNull(filter, "Filter");
        Checks.notNull(event, "Event");
        return new MessageFilterEvent(filter, event);
    }

    public @NotNull String getMessageRaw() {
        return getMessage().getContentRaw();
    }

    /**
     * @return true if the message is from AutoMod
     */
    public boolean isFromAutoMod() {
        return isFromType(MessageType.AUTO_MODERATION_ACTION);
    }

    public Filter getFilter() {
        return filter;
    }

    public boolean isFromType(@NotNull MessageType type) {
        return getMessage().getType() == type;
    }
}
