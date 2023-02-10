package testapp;

import dev.JustRed23.jdautils.component.SendableComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class HelloComponent extends SendableComponent {

    private Button deleteButton;
    private EmbedBuilder builder;

    public HelloComponent() {
        super("hello");
    }

    protected void onCreate() {
        deleteButton = Button.danger("delete", "Delete");

        builder = new EmbedBuilder();
        builder.setTitle("Hello, World!");
        builder.setDescription("This is a test component");
        builder.setFooter("This is a footer");
        builder.setColor(Color.GREEN);
    }

    protected void onRemove() {
        deleteButton = null;
        builder = null;
        LoggerFactory.getLogger(HelloComponent.class).info("Removed component: " + name);
    }

    public MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        return event.getChannel().sendMessageEmbeds(builder.build()).setActionRow(deleteButton);
    }

    public ReplyCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        return event.replyEmbeds(builder.build()).addActionRow(deleteButton);
    }
}
