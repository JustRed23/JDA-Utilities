package dev.JustRed23.jdautils.command;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.event.EventWatcher;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Internal use only</b><br>
 * Used to register slash commands with an {@link EventWatcher}
 */
@ApiStatus.Internal
public final class CommandComponent extends Component implements NoRegistry {

    private boolean contextCommand;
    private List<String> aliases;

    CommandComponent(String name) {
        super(name);
    }

    CommandComponent(String name, List<String> aliases) {
        this(name);
        this.aliases = new ArrayList<>(aliases);
    }

    CommandComponent(String name, List<String> aliases, String suffix) {
        this(name + suffix, aliases);
        this.aliases.replaceAll(s -> s + suffix);
    }

    public CommandComponent setContextCommand(boolean contextCommand) {
        this.contextCommand = contextCommand;
        return this;
    }

    public boolean isContextCommand() {
        return contextCommand;
    }

    public List<String> getAliases() {
        return aliases;
    }

    protected void onCreate() {}
    protected void onRemove() {}
}
