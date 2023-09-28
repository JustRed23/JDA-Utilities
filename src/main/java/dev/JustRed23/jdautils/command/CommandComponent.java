package dev.JustRed23.jdautils.command;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.event.EventWatcher;
import org.jetbrains.annotations.ApiStatus;

/**
 * <b>Internal use only</b><br>
 * Used to register slash commands with an {@link EventWatcher}
 */
@ApiStatus.Internal
public final class CommandComponent extends Component implements NoRegistry {

    private boolean contextCommand;

    CommandComponent(String name) {
        super(name);
    }

    public CommandComponent setContextCommand(boolean contextCommand) {
        this.contextCommand = contextCommand;
        return this;
    }

    public boolean isContextCommand() {
        return contextCommand;
    }

    protected void onCreate() {}
    protected void onRemove() {}
}
