package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.component.SendableComponent;
import dev.JustRed23.jdautils.registry.SendableComponentRegistry;
import dev.JustRed23.jdautils.settings.GuildSettingManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

public final class Builder {

    boolean ready = false;
    private final ListenerAdapter adapter;
    SendableComponentRegistry sendableComponentRegistry;
    GuildSettingManager guildSettingManager;

    Builder() {
        sendableComponentRegistry = new SendableComponentRegistry();
        adapter = new InternalEventListener(this);
    }

    /* INTERNAL */
    void freezeRegistries() {
        sendableComponentRegistry.freeze();
        ready = true;
    }

    void destroy() {
        sendableComponentRegistry.destroy();
        sendableComponentRegistry = null;

        if (guildSettingManager != null)
            guildSettingManager.shutdown();

        JDAUtilities.builder = null;
    }
    /* INTERNAL */

    public Builder withGuildSettingManager(GuildSettingManager guildSettingManager) {
        if (ready)
            throw new IllegalStateException("JDA Utilities is already ready, you must call this method before JDA initialization");

        this.guildSettingManager = guildSettingManager;
        return this;
    }

    public Builder registerComponent(Class<? extends SendableComponent> component) throws IllegalStateException {
        if (sendableComponentRegistry == null)
            throw new IllegalStateException("Builder was destroyed");

        if (component.equals(Component.class) || component.equals(SendableComponent.class))
            throw new IllegalArgumentException("Cannot register base component");

        if (Arrays.asList(component.getInterfaces()).contains(NoRegistry.class))
            return this;

        sendableComponentRegistry.register(component);
        return this;
    }

    @SafeVarargs
    public final Builder registerComponents(Class<? extends SendableComponent>... components) throws IllegalStateException {
        if (sendableComponentRegistry == null)
            throw new IllegalStateException("Builder was destroyed");

        Arrays.asList(components).forEach(this::registerComponent);
        return this;
    }

    public ListenerAdapter listener() {
        return adapter;
    }
}
