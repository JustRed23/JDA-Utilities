package testapp;

import dev.JustRed23.jdautils.JDAUtilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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

        instance.updateCommands().addCommands(Commands.slash("test", "Test command").setGuildOnly(true)).queue();
    }

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("test"))
            JDAUtilities.createComponent(HelloComponent.class).send(event);
        if (event.getMessage().getContentRaw().equals("shutdown"))
            event.getJDA().shutdown();
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("test"))
            JDAUtilities.createComponent("hello").reply(event);
    }

    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("delete"))
            event.getMessage().delete().queue();
    }
}
