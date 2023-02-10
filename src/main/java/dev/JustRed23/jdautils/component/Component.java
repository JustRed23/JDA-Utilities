package dev.JustRed23.jdautils.component;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class Component {

    protected UUID uuid;
    protected final String name;
    protected Guild guild;
    protected long messageId = -1;

    protected Component(String name) {
        this.name = name;
    }

    protected abstract void onCreate();
    protected abstract void onRemove();
    public abstract MessageCreateAction onSend(@NotNull MessageReceivedEvent event);
    public abstract ReplyCallbackAction onReply(@NotNull SlashCommandInteractionEvent event);

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
        guild = null;
        messageId = -1;
        uuid = null;
    }

    public final @Nullable Message send(@NotNull MessageReceivedEvent event) {
        if (!isCreated())
            return null;

        MessageCreateAction messageCreateAction = onSend(event);

        if (messageCreateAction == null)
            return null;

        Message hook = messageCreateAction.complete();
        guild = hook.getGuild();
        messageId = hook.getIdLong();
        return hook;
    }

    public final @Nullable InteractionHook reply(@NotNull SlashCommandInteractionEvent event) {
        if (!isCreated())
            return null;

        ReplyCallbackAction replyCallbackAction = onReply(event);

        if (replyCallbackAction == null)
            return null;

        InteractionHook hook = replyCallbackAction.complete();
        Message message = hook.retrieveOriginal().complete();
        guild = message.getGuild();
        messageId = message.getIdLong();
        return hook;
    }

    public final boolean isCreated() {
        return uuid != null;
    }

    public final boolean isSent() {
        return messageId != -1 && guild != null;
    }

    public final UUID getUuid() {
        return uuid;
    }

    public final String getName() {
        return name;
    }

    public final long getMessageId() {
        return messageId;
    }

    public Guild getGuild() {
        return guild;
    }
}
