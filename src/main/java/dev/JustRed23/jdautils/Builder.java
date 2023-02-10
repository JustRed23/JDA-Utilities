package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.registry.ComponentRegistry;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

public final class Builder {

    boolean ready = false;
    private final ListenerAdapter adapter;
    ComponentRegistry componentRegistry;

    Builder() {
        componentRegistry = new ComponentRegistry();
        adapter = new InternalEventListener(this);
    }

    /* INTERNAL */
    void freezeRegistries() {
        componentRegistry.freeze();
        ready = true;
    }

    void destroy() {
        componentRegistry.destroy();
        componentRegistry = null;
        JDAUtilities.builder = null;
    }
    /* INTERNAL */

    public Builder registerComponent(Class<? extends Component> component) throws IllegalStateException {
        if (componentRegistry == null)
            throw new IllegalStateException("Builder was destroyed");

        if (component.equals(Component.class))
            throw new IllegalArgumentException("Cannot register Component.class");

        componentRegistry.register(component);
        return this;
    }

    @SafeVarargs
    public final Builder registerComponents(Class<? extends Component>... components) throws IllegalStateException {
        if (componentRegistry == null)
            throw new IllegalStateException("Builder was destroyed");

        Arrays.asList(components).forEach(this::registerComponent);
        return this;
    }

    public ListenerAdapter listener() {
        return adapter;
    }
}
