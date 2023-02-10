package dev.JustRed23.jdautils.registry;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.SendableComponent;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class SendableComponentRegistry implements IRegistry<Class<? extends SendableComponent>> {

    private boolean frozen = false;
    private final Map<Class<? extends SendableComponent>, String> components;
    private final List<SendableComponent> instances = new ArrayList<>();

    public SendableComponentRegistry() {
        this.components = new HashMap<>();
    }

    public void register(Class<? extends SendableComponent> object) {
        if (frozen)
            throw new IllegalStateException("Registry is frozen");

        if (components.containsKey(object))
            throw new IllegalArgumentException("Component already registered");

        try {
            components.put(object, object.getDeclaredConstructor().newInstance().getName());
        } catch (Exception e) {
            LoggerFactory.getLogger(SendableComponentRegistry.class).error("Failed to register component", e);
        }
    }

    public void unregister(Class<? extends SendableComponent> object) {
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

    public Map<Class<? extends SendableComponent>, String> getComponents() {
        return Collections.unmodifiableMap(components);
    }

    public List<SendableComponent> getInstances() {
        return instances;
    }

    public SendableComponent create(Class<? extends SendableComponent> component) {
        try {
            SendableComponent instance = component.getDeclaredConstructor().newInstance();
            instances.add(instance);
            return instance.create();
        } catch (Exception e) {
            LoggerFactory.getLogger(SendableComponentRegistry.class).error("Failed to create component", e);
        }
        return null;
    }
}
