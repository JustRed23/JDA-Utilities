package dev.JustRed23.jdautils.component.interact;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.event.EventWatcher;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public abstract class SmartDropdown<T extends SelectMenu> extends Component implements NoRegistry {

    protected SmartDropdown(String name) {
        super(name);
    }

    public abstract T build();
    protected abstract void onCreate();
    protected abstract void onRemove();

    @NotNull
    @Contract("_ -> new")
    public static StringSelect create(StringSelectMenu.Builder builder) {
        return new StringSelect(builder);
    }

    @NotNull
    @Contract("_ -> new")
    public static EntitySelect create(EntitySelectMenu.Builder builder) {
        return new EntitySelect(builder);
    }

    public static class StringSelect extends SmartDropdown<StringSelectMenu> implements NoRegistry {

        private final StringSelectMenu.Builder builder;
        private final EventWatcher eventWatcher;
        private StringSelectMenu parent;

        private StringSelect(StringSelectMenu.Builder builder) {
            super("SmartStringDropdown");
            this.builder = builder;

            eventWatcher = new EventWatcher(this, StringSelectInteractionEvent.class);
            super.create();
        }

        public StringSelect withListener(EventWatcher.Listener<StringSelectInteractionEvent> listener) {
            eventWatcher.setListener(listener);
            return this;
        }

        public StringSelect withListener(EventWatcher.Listener<StringSelectInteractionEvent> listener, int expireAfter, TimeUnit unit) {
            eventWatcher.setListener(listener, expireAfter, unit);
            return this;
        }

        protected void onCreate() {
            parent = builder.setId(getUuid().toString()).build();
        }

        protected void onRemove() {
            eventWatcher.destroy();
            parent = null;
        }

        public StringSelectMenu build() {
            return parent;
        }
    }

    public static class EntitySelect extends SmartDropdown<EntitySelectMenu> implements NoRegistry {

        private final EntitySelectMenu.Builder builder;
        private final EventWatcher eventWatcher;
        private EntitySelectMenu parent;

        private EntitySelect(EntitySelectMenu.Builder builder) {
            super("SmartEntityDropdown");
            this.builder = builder;

            eventWatcher = new EventWatcher(this, EntitySelectInteractionEvent.class);
            super.create();
        }

        public EntitySelect withListener(EventWatcher.Listener<EntitySelectInteractionEvent> listener) {
            eventWatcher.setListener(listener);
            return this;
        }

        public EntitySelect withListener(EventWatcher.Listener<EntitySelectInteractionEvent> listener, int expireAfter, TimeUnit unit) {
            eventWatcher.setListener(listener, expireAfter, unit);
            return this;
        }

        protected void onCreate() {
            parent = builder.setId(getUuid().toString()).build();
        }

        protected void onRemove() {
            eventWatcher.destroy();
            parent = null;
        }

        public EntitySelectMenu build() {
            return parent;
        }
    }
}
