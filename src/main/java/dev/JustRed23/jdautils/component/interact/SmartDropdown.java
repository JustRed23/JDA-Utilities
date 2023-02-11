package dev.JustRed23.jdautils.component.interact;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.event.EventWatcher;
import dev.JustRed23.jdautils.event.WatcherManager;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class SmartDropdown extends Component {

    private StringSelectMenu.Builder builder;
    private StringSelectMenu parent;
    private EventWatcher eventWatcher;

    public SmartDropdown(StringSelectMenu.Builder builder) {
        super("SmartDropdown");
        this.builder = builder;

        eventWatcher = new EventWatcher(this, StringSelectInteractionEvent.class);
        WatcherManager.addWatcher(eventWatcher);
        super.create();
    }

    public static SmartDropdown create(StringSelectMenu.Builder builder) {
        return new SmartDropdown(builder);
    }

    public SmartDropdown withListener(EventWatcher.Listener<StringSelectInteractionEvent> listener) {
        eventWatcher.setListener(listener);
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
