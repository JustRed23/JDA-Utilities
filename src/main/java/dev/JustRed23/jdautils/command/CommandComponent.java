package dev.JustRed23.jdautils.command;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.event.EventWatcher;

/**
 * <b>Internal use only</b><br>
 * Used to register slash commands with an {@link EventWatcher}
 */
public final class CommandComponent extends Component implements NoRegistry {

    CommandComponent(String name) {
        super(name);
    }

    protected void onCreate() {}
    protected void onRemove() {}
}
