package dev.JustRed23.jdautils.registry;

import dev.JustRed23.jdautils.component.Component;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class ComponentRegistry implements IRegistry<Class<? extends Component>> {

    private boolean frozen = false;
    private final Map<Class<? extends Component>, String> components;
    private final List<Component> instances = new ArrayList<>();

    public ComponentRegistry() {
        this.components = new HashMap<>();
    }

    public void register(Class<? extends Component> object) {
        if (frozen)
            throw new IllegalStateException("Registry is frozen");

        if (components.containsKey(object))
            throw new IllegalArgumentException("Component already registered");

        try {
            components.put(object, object.getDeclaredConstructor().newInstance().getName());
        } catch (Exception e) {
            LoggerFactory.getLogger(ComponentRegistry.class).error("Failed to register component", e);
        }
    }

    public void unregister(Class<? extends Component> object) {
        if (frozen)
            throw new IllegalStateException("Registry is frozen");
        components.remove(object);
    }

    public void destroy() {
        unfreeze();
        components.clear();
        instances.forEach(Component::remove);
        instances.clear();
    }

    public void freeze() {
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }

    public Map<Class<? extends Component>, String> getComponents() {
        return Collections.unmodifiableMap(components);
    }

    public List<Component> getInstances() {
        return instances;
    }

    public Component create(Class<? extends Component> component) {
        try {
            Component instance = component.getDeclaredConstructor().newInstance();
            instances.add(instance);
            return instance.create();
        } catch (Exception e) {
            LoggerFactory.getLogger(ComponentRegistry.class).error("Failed to create component", e);
        }
        return null;
    }
}
