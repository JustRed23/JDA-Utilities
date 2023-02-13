package dev.JustRed23.jdautils.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class Component {

    protected UUID uuid;
    protected final String name;
    private @Nullable Object identifier;

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

    /**
     * Useful for identifying the component between multiple components of the same type
     * <p><b>NOTE: </b> The identifier must be unique</p>
     * @param identifier The identifier, can be anything
     */
    public void setIdentifier(@Nullable Object identifier) {
        ComponentIdentifier.checkUnique(identifier);
        this.identifier = identifier;
    }

    public @Nullable Object getIdentifier() {
        return identifier;
    }

    public final UUID getUuid() {
        return uuid;
    }

    public final String getName() {
        return name;
    }
}
