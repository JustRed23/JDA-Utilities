package dev.JustRed23.jdautils.component.interact;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.component.SendableComponent;
import dev.JustRed23.jdautils.event.EventWatcher;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SmartModal extends SendableComponent implements NoRegistry {

    private final Modal.Builder builder;
    private final EventWatcher<ModalInteractionEvent> eventWatcher;
    private Modal parent;

    protected SmartModal(Modal.Builder builder) {
        super("SmartModal");
        this.builder = builder;

        eventWatcher = new EventWatcher<>(this, ModalInteractionEvent.class, true);
    }

    public static SmartModal create(Modal.Builder builder) {
        SmartModal modal = new SmartModal(builder);
        modal.create();
        return modal;
    }

    public SmartModal withListener(EventWatcher.Listener<ModalInteractionEvent> listener) {
        eventWatcher.setListener(listener);
        return this;
    }

    public SmartModal withListener(EventWatcher.Listener<ModalInteractionEvent> listener, int expireAfter, TimeUnit unit) {
        eventWatcher.setListener(listener, expireAfter, unit);
        return this;
    }

    protected void onCreate() {
        parent = builder.setId(getUuid().toString()).build();
    }

    protected void onRemove() {
        eventWatcher.destroy();
        parent = null;
    }

    protected MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        throw new UnsupportedOperationException("This component can only be used in slash commands, use reply instead");
    }

    protected ModalCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        return event.replyModal(parent);
    }

    protected List<Component> getChildren() {
        return null;
    }

    public Modal build() {
        return parent;
    }
}
