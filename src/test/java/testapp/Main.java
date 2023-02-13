package testapp;

import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.command.SlashCommand;
import dev.JustRed23.jdautils.component.interact.SmartReaction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Properties;

public class Main extends ListenerAdapter {

    public static void main(String[] args) throws InterruptedException {
        Properties secrets = null;
        try (InputStream secretsFile = Main.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            secrets = new Properties();
            secrets.load(secretsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListenerAdapter listener = JDAUtilities.getInstance()
                .registerComponent(HelloComponent.class)
                .listener();

        JDA instance = JDABuilder.createDefault(secrets.getProperty("token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("with cats"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(listener, new Main())
                .build().awaitReady();

        instance.updateCommands().addCommands(
                SlashCommand.slash("test", "A simple test command")
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
        ).queue();
    }

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("test"))
            JDAUtilities.createComponent(HelloComponent.class).send(event);
        if (event.getMessage().getContentRaw().equals("shutdown"))
            event.getJDA().shutdown();
    }

    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("delete"))
            event.getMessage().delete().queue();
    }
}
