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

import java.util.List;
import java.util.UUID;

public abstract class SendableComponent extends Component {

    protected Guild guild;
    protected long messageId = -1;

    protected SendableComponent(String name) {
        super(name);
    }

    protected abstract MessageCreateAction onSend(@NotNull MessageReceivedEvent event);
    protected abstract ReplyCallbackAction onReply(@NotNull SlashCommandInteractionEvent event);
    protected abstract List<Component> getChildren();

    public @Nullable Component getChild(@NotNull String name) {
        for (Component component : getChildren()) {
            if (component.getName().equals(name))
                return component;
        }
        return null;
    }

    public @Nullable Component getChild(@NotNull UUID uuid) {
        for (Component component : getChildren()) {
            if (component.getUuid().equals(uuid))
                return component;
        }
        return null;
    }

    public @Nullable Component getChild(@NotNull String name, @NotNull Object identifier) {
        for (Component component : getChildren()) {
            if (component.getName().equals(name) && component.identifier != null && component.identifier.equals(identifier))
                return component;
        }
        return null;
    }

    public List<Component> getChildrenFromType(@NotNull Class<? extends Component> clazz) {
        List<Component> components = getChildren();
        components.removeIf(component -> !component.getClass().equals(clazz));
        return components;
    }

    public final @NotNull SendableComponent create() {
        super.create();
        return this;
    }

    public final void remove() {
        if (!isCreated())
            return;

        if (getChildren() != null)
            getChildren().forEach(Component::remove);

        super.remove();
        guild = null;
        messageId = -1;
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

    public final boolean isSent() {
        return messageId != -1 && guild != null;
    }

    public final long getMessageId() {
        return messageId;
    }

    public Guild getGuild() {
        return guild;
    }
}
