package dev.JustRed23.jdautils.component;

import java.util.UUID;

public abstract class Component {

    protected UUID uuid;
    protected final String name;

    protected Component(String name) {
        this.name = name;
    }

    protected abstract void create();
    protected abstract void remove();

    public final Component createAndGet() {
        if (uuid != null)
            return this;

        uuid = UUID.randomUUID();
        create();
        return this;
    }

    public final Component removeAndGet() {
        if (uuid == null)
            return this;

        uuid = null;
        remove();
        return this;
    }

    public final UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
