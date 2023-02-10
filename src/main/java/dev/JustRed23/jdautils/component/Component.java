package dev.JustRed23.jdautils.component;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class Component {

    protected UUID uuid;
    protected final String name;

    protected Component(String name) {
        this.name = name;
    }

    protected abstract void onCreate();
    protected abstract void onRemove();

    public @NotNull Component create() {
        if (isCreated())
            return this;

        uuid = UUID.randomUUID();
        onCreate();
        return this;
    }

    public void remove() {
        if (!isCreated())
            return;

        onRemove();
        uuid = null;
    }

    public final boolean isCreated() {
        return uuid != null;
    }

    public final UUID getUuid() {
        return uuid;
    }

    public final String getName() {
        return name;
    }
}
