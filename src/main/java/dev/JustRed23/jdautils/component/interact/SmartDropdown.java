package dev.JustRed23.jdautils.component.interact;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.event.EventWatcher;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.concurrent.TimeUnit;

public class SmartDropdown extends Component implements NoRegistry {

    private final StringSelectMenu.Builder builder;
    private final EventWatcher eventWatcher;
    private StringSelectMenu parent;

    public SmartDropdown(StringSelectMenu.Builder builder) {
        super("SmartDropdown");
        this.builder = builder;

        eventWatcher = new EventWatcher(this, StringSelectInteractionEvent.class);
        super.create();
    }

    public static SmartDropdown create(StringSelectMenu.Builder builder) {
        return new SmartDropdown(builder);
    }

    public SmartDropdown withListener(EventWatcher.Listener<StringSelectInteractionEvent> listener) {
        eventWatcher.setListener(listener);
        return this;
    }

    public SmartDropdown withListener(EventWatcher.Listener<ButtonInteractionEvent> listener, int expireAfter, TimeUnit unit) {
        eventWatcher.setListener(listener, expireAfter, unit);
        return this;
    }

    public StringSelectMenu build() {
        return parent;
    }

    protected void onCreate() {
        parent = builder.setId(getUuid().toString()).build();
    }

    protected void onRemove() {
        parent = null;
    }
}
