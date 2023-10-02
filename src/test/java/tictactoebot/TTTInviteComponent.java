package tictactoebot;

import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.SendableComponent;
import dev.JustRed23.jdautils.component.interact.SmartButton;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TTTInviteComponent extends SendableComponent {

    private final User player;
    private final User opponent;

    private SmartButton btnYes, btnNo;

    public TTTInviteComponent(User player, User opponent) {
        super("ttt-invite");
        this.player = player;
        this.opponent = opponent;
    }

    protected void onCreate() {
        btnYes = SmartButton.success("yes")
                .withListener(event -> {
                    if (!event.getUser().equals(opponent)) {
                        event.deferEdit().queue();
                        return;
                    }

                    event.deferEdit().queue();

                    final SendableComponent game = JDAUtilities.createComponent(TTTComponent.class, new Class[]{User.class, User.class}, player, opponent);
                    game.edit(event.getHook());

                    this.remove();
                    this.guild = game.getGuild();
                    this.messageId = game.getMessageId();
                });
        btnNo = SmartButton.danger("no")
                .withListener(event -> {
                    if (!event.getUser().equals(opponent)) {
                        event.deferEdit().queue();
                        return;
                    }

                    event.getChannel().retrieveMessageById(getMessageId()).complete().delete().queue();
                    event.getChannel().sendMessage(opponent.getEffectiveName() + " declined your invite.")
                            .map(hook -> hook.delete().queueAfter(5, TimeUnit.SECONDS)).queue();
                    this.remove();
                });
    }

    protected void onRemove() {
        btnYes = null;
        btnNo = null;
    }

    protected MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        return null;
    }

    protected InteractionCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        return event.reply(opponent.getAsMention() + ", " + player.getEffectiveName() + " wants to play Tic Tac Toe with you! Do you accept?")
                .addActionRow(btnYes.build(), btnNo.build());
    }

    protected List<Component> getChildren() {
        return List.of(btnYes, btnNo);
    }
}
