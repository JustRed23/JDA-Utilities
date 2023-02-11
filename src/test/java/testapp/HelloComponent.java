package testapp;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.SendableComponent;
import dev.JustRed23.jdautils.component.interact.SmartButton;
import dev.JustRed23.jdautils.component.interact.SmartDropdown;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HelloComponent extends SendableComponent {

    private SmartButton deleteButton, randomButton;
    private SmartDropdown select;
    private EmbedBuilder builder;

    public HelloComponent() {
        super("hello");
    }

    protected void onCreate() {
        deleteButton = SmartButton.danger("Delete")
                .withListener(event -> event.getMessage().delete().queue());

        randomButton = SmartButton.primary("Random")
                .withListener(event -> event.reply("Randomness happened!").setEphemeral(true).queue(ih -> ih.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)), 15, TimeUnit.SECONDS);

        select = SmartDropdown.create(StringSelectMenu.create("gender")
                        .setPlaceholder("Select your gender")
                        .addOption("Male", "male")
                        .addOption("Female", "female")
                        .addOption("Samsung Galaxy XCover 5 sim card", "samsung")
                ).withListener(event -> event.reply("You selected: " + event.getSelectedOptions().get(0).getLabel()).setEphemeral(true).queue(ih -> ih.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)));

        builder = new EmbedBuilder();
        builder.setTitle("Hello, World!");
        builder.setDescription("This is a test component");
        builder.setFooter("This is a footer");
        builder.setColor(Color.GREEN);
    }

    protected void onRemove() {
        deleteButton = null;
        randomButton = null;
        select = null;
        builder = null;
        LoggerFactory.getLogger(HelloComponent.class).info("Removed component: " + name);
    }

    protected List<Component> getChildren() {
        return Arrays.asList(deleteButton, randomButton, select);
    }

    public MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        return event.getChannel().sendMessageEmbeds(builder.build()).addActionRow(select.build()).addActionRow(randomButton.build(), deleteButton.build());
    }

    public ReplyCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        return event.replyEmbeds(builder.build()).addActionRow(select.build()).addActionRow(randomButton.build(), deleteButton.build());
    }
}
