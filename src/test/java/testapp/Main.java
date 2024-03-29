package testapp;

import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.command.Command;
import dev.JustRed23.jdautils.component.interact.SmartModal;
import dev.JustRed23.jdautils.component.interact.SmartReaction;
import dev.JustRed23.jdautils.event.WatcherManager;
import dev.JustRed23.jdautils.message.Filter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Filter FILTER = new SimpleMessageFilter().withListener(event -> {
        event.getGuildChannel().sendMessage("Woah! Slow down with the curses")
                .map(reply -> reply.delete().queueAfter(5, TimeUnit.SECONDS))
                .queue();
    });

    public static void main(String[] args) throws InterruptedException {
        Properties secrets = null;
        try (InputStream secretsFile = Main.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            secrets = new Properties();
            secrets.load(secretsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListenerAdapter listener = JDAUtilities.getInstance().listener();

        JDA instance = JDABuilder.createDefault(secrets.getProperty("token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("with cats"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(listener)
                .build().awaitReady();

        instance.updateCommands().addCommands(
                Command.slash("test", "A simple test command")
                        .executes(event -> JDAUtilities.createComponent(HelloComponent.class).reply(event))
                        .build()
                        .setGuildOnly(true), // You can still modify the data after building
                JDAUtilities.createSlashCommand("testsub", "A simple test command to test sub commands") // You can also use the JDAUtilities method
                        .addSubCommand("sub1", "My first sub command")
                            .executes(event -> event.reply("Sub command 1").setEphemeral(true).queue())
                            .build()
                        .addSubCommand("sub2", "My second sub command")
                            .executes(event -> event.reply("Sub command 2").setEphemeral(true).queue())
                            .build()
                        .modifyData(data -> data.setGuildOnly(true)) // This is just to show that you can modify the data
                        .build(),
                JDAUtilities.createSlashCommand("testemotes", "Test the emote listener")
                        .executes(event ->
                                SmartReaction.create("This is a test!")
                                        .addReaction("1\uFE0F\u20E3")
                                        .addReaction("2\uFE0F\u20E3")
                                        .addReaction("3\uFE0F\u20E3")
                                        .addReaction("4\uFE0F\u20E3")
                                        .addReaction("5\uFE0F\u20E3")
                                        .addReaction("6\uFE0F\u20E3")
                                        .addReaction("7\uFE0F\u20E3")
                                        .addReaction("\u274C")
                                        .withListeners(add -> {
                                            add.getChannel().sendMessage("You added " + add.getEmoji().getAsReactionCode()).queue();
                                        }, remove -> {
                                            remove.getChannel().sendMessage("You removed " + remove.getEmoji().getAsReactionCode()).queue();
                                        })
                                        .reply(event)
                        ).build()
                        .setGuildOnly(true),
                JDAUtilities.createSlashCommand("testmodal", "Test the modal listener")
                        .executes(event ->
                                SmartModal.create(Modal.create("test-modal", "Welcome to my test modal!")
                                                .addActionRow(TextInput.create("test-input", "Enter some text", TextInputStyle.SHORT).build())
                                        )
                                        .withListener(modalEvent -> modalEvent.reply(modalEvent.getMember().getEffectiveName() + ", you typed `" + modalEvent.getInteraction().getValue("test-input").getAsString() + "`").queue())
                                        .reply(event)
                        ).build()
                        .setGuildOnly(true),
                JDAUtilities.createSlashCommand("checkwatchers", "Check which watchers are active")
                        .executes(event -> event.reply(WatcherManager.getStatus()).queue())
                        .build()
                        .setGuildOnly(true),
                JDAUtilities.createMessageContextCommand("say message")
                        .executes(event -> event.reply(event.getTarget().getContentRaw()).queue())
                        .build(),
                JDAUtilities.createUserContextCommand("say user")
                        .executes(event -> event.reply(event.getTarget().getAsTag()).queue())
                        .build()
        ).queue();

        Guild testguild = Objects.requireNonNull(instance.getGuildById(secrets.getProperty("test-guild")));

        JDAUtilities.getGuildFilterManager(testguild)
                .addFilter(FILTER);

        JDAUtilities.addGuildMessageListener(testguild, event -> System.out.println(event.getAuthor().getName() + " > " + event.getMessage().getContentRaw()));
    }
}
