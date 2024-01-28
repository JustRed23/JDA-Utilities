package dev.JustRed23.jdautils.command;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import org.jetbrains.annotations.ApiStatus;

/**
 * <b>Internal use only</b><br>
 * Used to register autocomplete listeners for command options
 */
@ApiStatus.Internal
public class OptionComponent extends Component implements NoRegistry {

    public OptionComponent(String name) {
        super(name);
    }

    protected void onCreate() {}
    protected void onRemove() {}
}
