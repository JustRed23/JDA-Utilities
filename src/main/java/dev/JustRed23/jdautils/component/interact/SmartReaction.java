package dev.JustRed23.jdautils.component.interact;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.component.SendableComponent;
import dev.JustRed23.jdautils.event.EventWatcher;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SmartReaction extends SendableComponent implements NoRegistry {

    private final List<Emoji> reactions;
    private EventWatcher onAdd, onRemove;
    private String message;
    private MessageEmbed embed;

    private SmartReaction() {
        super("SmartReaction");
        reactions = new ArrayList<>();
    }

    @NotNull
    public static SmartReaction create(String message) {
        SmartReaction reaction = new SmartReaction();
        reaction.message = message;
        reaction.create();
        return reaction;
    }

    @NotNull
    public static SmartReaction create(MessageEmbed embed) {
        SmartReaction reaction = new SmartReaction();
        reaction.embed = embed;
        reaction.create();
        return reaction;
    }

    public SmartReaction addReaction(Emoji emoji) {
        reactions.add(emoji);
        return this;
    }

    public SmartReaction addReaction(String emoji) {
        reactions.add(Emoji.fromFormatted(emoji));
        return this;
    }

    public SmartReaction withListeners(EventWatcher.Listener<MessageReactionAddEvent> onAdd, EventWatcher.Listener<MessageReactionRemoveEvent> onRemove) {
        this.onAdd.setListener(event -> {
            MessageReactionAddEvent addEvent = (MessageReactionAddEvent) event;
            EmojiUnion emoji = addEvent.getEmoji();
            if (getReactions().contains(emoji))
                onAdd.onEvent(addEvent);
            else
                addEvent.getReaction().removeReaction(addEvent.retrieveUser().complete()).queue();
        });
        this.onRemove.setListener(event -> {
            MessageReactionRemoveEvent removeEvent = (MessageReactionRemoveEvent) event;
            EmojiUnion emoji = removeEvent.getEmoji();
            if (getReactions().contains(emoji))
                onRemove.onEvent(removeEvent);
        });
        return this;
    }

    public SmartReaction withListeners(EventWatcher.Listener<MessageReactionAddEvent> onAdd, EventWatcher.Listener<MessageReactionRemoveEvent> onRemove, int expireAfter, TimeUnit unit) {
        this.onAdd.setListener(event -> {
            MessageReactionAddEvent addEvent = (MessageReactionAddEvent) event;
            EmojiUnion emoji = addEvent.getEmoji();
            if (getReactions().contains(emoji))
                onAdd.onEvent(addEvent);
            else
                addEvent.getReaction().removeReaction(addEvent.retrieveUser().complete()).queue();
        }, expireAfter, unit);
        this.onRemove.setListener(event -> {
            MessageReactionRemoveEvent removeEvent = (MessageReactionRemoveEvent) event;
            EmojiUnion emoji = removeEvent.getEmoji();
            if (getReactions().contains(emoji))
                onRemove.onEvent(removeEvent);
        }, expireAfter, unit);
        return this;
    }

    protected void onCreate() {
        onAdd = new EventWatcher(this, MessageReactionAddEvent.class);
        onRemove = new EventWatcher(this, MessageReactionRemoveEvent.class);
    }

    protected void onRemove() {
        onAdd.destroy();
        onRemove.destroy();
    }

    protected MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        if (getReactions().isEmpty())
            throw new IllegalStateException("No reactions added to the SmartReaction");
        if (message != null)
            return event.getChannel().sendMessage(message);
        else
            return event.getChannel().sendMessageEmbeds(embed);
    }

    protected ReplyCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        if (getReactions().isEmpty())
            throw new IllegalStateException("No reactions added to the SmartReaction");
        if (message != null)
            return event.reply(message);
        else
            return event.replyEmbeds(embed);
    }

    protected void onSent(@NotNull Message message) {
        for (Emoji emoji : reactions)
            message.addReaction(emoji).queue();
    }

    protected List<Component> getChildren() {
        return null;
    }

    public List<Emoji> getReactions() {
        return reactions;
    }
}
