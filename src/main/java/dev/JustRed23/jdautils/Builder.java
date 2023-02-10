package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.SendableComponent;
import dev.JustRed23.jdautils.registry.SendableComponentRegistry;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

public final class Builder {

    boolean ready = false;
    private final ListenerAdapter adapter;
    SendableComponentRegistry sendableComponentRegistry;

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
        JDAUtilities.builder = null;
    }
    /* INTERNAL */

    public Builder registerComponent(Class<? extends SendableComponent> component) throws IllegalStateException {
        if (sendableComponentRegistry == null)
            throw new IllegalStateException("Builder was destroyed");

        if (component.equals(Component.class) || component.equals(SendableComponent.class))
            throw new IllegalArgumentException("Cannot register base component");

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
