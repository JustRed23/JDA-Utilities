package dev.JustRed23.jdautils.component;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
    public abstract void send(@NotNull MessageReceivedEvent event);
    public abstract void reply(@NotNull SlashCommandInteractionEvent event);

    public final @NotNull Component create() {
        if (isCreated())
            return this;

        uuid = UUID.randomUUID();
        onCreate();
        return this;
    }

    public final void remove() {
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

    public String getName() {
        return name;
    }
}
