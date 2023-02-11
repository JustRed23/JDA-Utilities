package dev.JustRed23.jdautils.registry;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.component.SendableComponent;
import org.jetbrains.annotations.Nullable;
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

    @Nullable
    public SendableComponent create(Class<? extends SendableComponent> componentClass) {
        Class<? extends SendableComponent> component = getComponents()
                .keySet()
                .stream()
                .filter(it -> it.equals(componentClass))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Component with name " + componentClass.getSimpleName() + " does not exist"));

        if (Arrays.asList(component.getInterfaces()).contains(NoRegistry.class))
            return null;

        try {
            SendableComponent instance = component.getDeclaredConstructor().newInstance();
            instances.add(instance);
            return instance.create();
        } catch (Exception e) {
            LoggerFactory.getLogger(SendableComponentRegistry.class).error("Failed to create component", e);
        }
        return null;
    }

    @Nullable
    public SendableComponent create(String componentName) {
        Class<? extends SendableComponent> component = getComponents()
                .entrySet()
                .stream()
                .filter(it -> it.getValue().equals(componentName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Component with name " + componentName + " does not exist"))
                .getKey();

        if (Arrays.asList(component.getInterfaces()).contains(NoRegistry.class))
            return null;

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
