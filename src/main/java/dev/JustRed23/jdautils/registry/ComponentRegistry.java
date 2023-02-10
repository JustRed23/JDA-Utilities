package dev.JustRed23.jdautils.registry;

import dev.JustRed23.jdautils.component.Component;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class ComponentRegistry implements IRegistry<Class<? extends Component>> {

    private boolean frozen = false;
    private final List<Class<? extends Component>> components;
    private final List<Component> instances = new ArrayList<>();

    public ComponentRegistry() {
        this.components = new ArrayList<>();
    }

    public void register(Class<? extends Component> object) {
        if (frozen)
            throw new IllegalStateException("Registry is frozen");

        if (components.contains(object))
            throw new IllegalArgumentException("Component already registered");

        components.add(object);
    }

    public void unregister(Class<? extends Component> object) {
        if (frozen)
            throw new IllegalStateException("Registry is frozen");
        components.remove(object);
    }

    public void destroy() {
        unfreeze();
        components.clear();
        instances.forEach(Component::removeAndGet);
        instances.clear();
    }

    public void freeze() {
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }

    public List<Class<? extends Component>> getComponents() {
        return components;
    }

    public Component create(Class<? extends Component> component) {
        try {
            Component instance = component.getDeclaredConstructor().newInstance();
            instances.add(instance);
            return instance.createAndGet();
        } catch (Exception e) {
            LoggerFactory.getLogger(ComponentRegistry.class).error("Failed to create component", e);
        }
        return null;
    }
}
