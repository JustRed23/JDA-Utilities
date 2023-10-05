import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.command.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandTest {

    private static Properties secrets;

    @BeforeAll
    static void getProperties() {
        try (InputStream secretsFile = BaseTest.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            secrets = new Properties();
            secrets.load(secretsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSlashCommand() {
        SlashCommandData basic = Command.slash("test-autoregister", "a simple test command")
                .executes(event -> event.reply("command executed").queue())
                .build();

        assertNotNull(basic);

        SlashCommandData subCommand = Command.slash("test2", "a simple test command")
                .addSubCommand("sub", "a sub command")
                    .executes(event -> event.reply("sub command executed").queue())
                    .build()
                .addSubCommand("sub2", "another sub command")
                    .executes(event -> event.reply("second sub command executed").queue())
                    .build()
                .build();

        assertNotNull(subCommand);

        assertThrows(IllegalStateException.class, () ->
                Command.slash("test3", "a simple test command")
                    .addSubCommand("sub", "a sub command")
                        .executes(event -> event.reply("sub command executed").queue())
                        .build()
                    .executes(event -> event.reply("command executed").queue())
                    .build()
        );
    }

    @Test
    void testSlashCommandRegistering() throws InterruptedException {
        Command.slash("test", "a simple test command")
                .executes(event -> event.reply("automatically registered command executed!").queue())
                .buildAndRegister();

        // Create a new JDA instance with the builder
        ListenerAdapter listener = JDAUtilities.getInstance().listener();

        JDA instance = JDABuilder.createDefault(secrets.getProperty("token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("with cats"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(listener)
                .build().awaitReady();

        Thread.sleep(5000);

        instance.shutdown();
    }

    @Test
    void testCommandConditions() throws InterruptedException {
        JDAUtilities.createSlashCommand("some-admin-command", "An admin command that does admin stuff")
                .addCondition(event -> {
                    if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                        event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
                        return false;
                    }

                    return true;
                })
                .executes(event -> event.reply("Admin command executed!").queue())
                .buildAndRegister();

        JDAUtilities.createUserContextCommand("another-admin-command")
                .addCondition(event -> {
                    if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                        event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
                        return false;
                    }

                    return true;
                })
                .executes(event -> event.reply("Admin user command executed!").queue())
                .buildAndRegister();

        JDAUtilities.createMessageContextCommand("yet-another-admin-command")
                .addCondition(event -> {
                    if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                        event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
                        return false;
                    }

                    return true;
                })
                .executes(event -> event.reply("Admin message command executed!").queue())
                .buildAndRegister();

        // Create a new JDA instance with the builder
        ListenerAdapter listener = JDAUtilities.getInstance().listener();

        JDA instance = JDABuilder.createDefault(secrets.getProperty("token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("with cats"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(listener)
                .build().awaitReady();

        Thread.sleep(20_000);

        instance.shutdown();
    }
}
