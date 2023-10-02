package dev.JustRed23.jdautils.component;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class SendableComponent extends Component {

    private static final List<SendableComponent> instances = new ArrayList<>();

    public static @Nullable SendableComponent create(@NotNull Class<? extends SendableComponent> componentClass) {
        if (Arrays.asList(componentClass.getInterfaces()).contains(NoRegistry.class))
            throw new IllegalArgumentException("Component class is annotated with NoRegistry and cannot be created");

        try {
            SendableComponent component = componentClass.getDeclaredConstructor().newInstance();
            instances.add(component);
            return component.create();
        } catch (Exception e) {
            LoggerFactory.getLogger(SendableComponent.class).error("Failed to create component", e);
        }
        return null;
    }

    public static @Nullable SendableComponent create(@NotNull Class<? extends SendableComponent> componentClass, Class<?>[] paramTypes, Object... constructorArgs) {
        if (Arrays.asList(componentClass.getInterfaces()).contains(NoRegistry.class))
            throw new IllegalArgumentException("Component class is annotated with NoRegistry and cannot be created");

        try {
            SendableComponent component = componentClass.getConstructor(paramTypes).newInstance(constructorArgs);
            instances.add(component);
            return component.create();
        } catch (Exception e) {
            LoggerFactory.getLogger(SendableComponent.class).error("Failed to create component", e);
        }
        return null;
    }

    public static List<SendableComponent> getInstances() {
        return instances;
    }

    protected Guild guild;
    protected long messageId = -1;
    private boolean modal = false;

    protected SendableComponent(String name) {
        super(name);
    }

    protected abstract MessageCreateAction onSend(@NotNull MessageReceivedEvent event);
    protected abstract InteractionCallbackAction onReply(@NotNull SlashCommandInteractionEvent event);
    protected WebhookMessageEditAction<Message> onEdit(@NotNull InteractionHook hook) { return null; }
    protected void onSent(@NotNull Message message) {}
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
            if (component.getName().equals(name) && component.getIdentifier() != null && component.getIdentifier().equals(identifier))
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
        if (!isCreated() || isSent())
            return null;

        MessageCreateAction messageCreateAction = onSend(event);

        if (messageCreateAction == null)
            return null;

        Message hook = messageCreateAction.complete();
        guild = hook.getGuild();
        messageId = hook.getIdLong();
        onSent(hook);
        return hook;
    }

    public final @Nullable InteractionHook reply(@NotNull SlashCommandInteractionEvent event) {
        if (!isCreated() || isSent())
            return null;

        InteractionCallbackAction<?> interactionCallbackAction = onReply(event);

        if (interactionCallbackAction == null)
            return null;

        if (interactionCallbackAction instanceof ModalCallbackAction modalAction) {
            modalAction.queue();
            modal = true;
            return null;
        }

        ReplyCallbackAction replyCallbackAction;
        try {
            replyCallbackAction = (ReplyCallbackAction) interactionCallbackAction;
        } catch (ClassCastException ignored) {
            throw new IllegalArgumentException("InteractionCallbackAction must be of type ReplyCallbackAction or ModalCallbackAction");
        }

        InteractionHook hook = replyCallbackAction.complete();
        Message message = hook.retrieveOriginal().complete();
        guild = message.getGuild();
        messageId = message.getIdLong();
        onSent(message);
        return hook;
    }

    public final void edit(@NotNull InteractionHook hook) {
        if (!isCreated() || isSent())
            return;

        WebhookMessageEditAction<Message> editAction = onEdit(hook);

        if (editAction == null)
            return;

        Message message = editAction.complete();
        guild = message.getGuild();
        messageId = message.getIdLong();
        onSent(message);
        getChildren().stream().filter(SendableComponent.class::isInstance).forEach(component -> ((SendableComponent) component).onSent(message));
    }

    public final void restore(@NotNull Message message) {
        if (!isCreated())
            return;

        guild = message.getGuild();
        messageId = message.getIdLong();
    }

    public final boolean isSent() {
        return (messageId != -1 && guild != null) || modal;
    }

    public final long getMessageId() {
        return messageId;
    }

    public Guild getGuild() {
        return guild;
    }
}
