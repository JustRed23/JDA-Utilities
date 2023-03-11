package testapp;

import dev.JustRed23.jdautils.event.custom.MessageFilterEvent;
import dev.JustRed23.jdautils.message.Filter;
import org.jetbrains.annotations.NotNull;

public class SimpleMessageFilter extends Filter {

    protected SimpleMessageFilter() {
        super("simple-message-filter", "Filters out messages that contain the word 'frick'");
    }

    protected boolean onMessageReceived(@NotNull MessageFilterEvent event) {
        if (event.isFromAutoMod())
            return false;
        return event.getMessage().getContentRaw().equalsIgnoreCase("frick");
    }
}
