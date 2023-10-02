package tictactoebot;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.SendableComponent;
import dev.JustRed23.jdautils.component.interact.SmartReaction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TTTComponent extends SendableComponent {

    public TTTLogic logic;
    public User player;
    public User opponent;

    public EmbedBuilder builder;
    public SmartReaction reaction;

    public TTTComponent(User player, User opponent) {
        super("tictactoe");
        this.player = player;
        this.opponent = opponent;
    }

    protected void onCreate() {
        builder = new EmbedBuilder();
        builder.setTitle("Tic Tac Toe");

        logic = new TTTLogic(this);
        builder.setDescription(logic.generateDescription());

        reaction = SmartReaction.create(builder.build())
                .addReaction("1\uFE0F\u20E3")
                .addReaction("2\uFE0F\u20E3")
                .addReaction("3\uFE0F\u20E3")
                .addReaction("4\uFE0F\u20E3")
                .addReaction("5\uFE0F\u20E3")
                .addReaction("6\uFE0F\u20E3")
                .addReaction("7\uFE0F\u20E3")
                .addReaction("8\uFE0F\u20E3")
                .addReaction("9\uFE0F\u20E3");

        reaction.withListeners(add -> {
            if (!reaction.isSent()) return;

            Emoji emoji = add.getEmoji();
            User player = add.retrieveUser().complete();
            final Message message = add.retrieveMessage().complete();

            if (logic.invalidMove(player)) {
                message.removeReaction(emoji, player).queue();
                return;
            }

            reaction.getReactions().remove(emoji);
            message.clearReactions(emoji).queue();
            logic.makeMove(Integer.parseInt(emoji.getAsReactionCode().substring(0, 1)), player);

            if (logic.checkGameState(player)) {
                message.editMessageEmbeds(builder.build()).queue();
                message.clearReactions().queue();
                reaction.remove();
            } else message.editMessageEmbeds(builder.build()).queue();
        }, remove -> {});
    }

    protected void onRemove() {
        reaction = null;
        builder = null;
    }

    protected MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        return null;
    }

    protected InteractionCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        return null;
    }

    protected WebhookMessageEditAction<Message> onEdit(@NotNull InteractionHook hook) {
        return hook.editOriginalEmbeds(builder.build()).setComponents().setContent("");
    }

    protected void onSent(@NotNull Message message) {
        reaction.restore(message);
    }

    protected List<Component> getChildren() {
        return List.of(reaction);
    }
}
