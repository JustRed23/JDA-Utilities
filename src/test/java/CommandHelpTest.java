import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.command.CommandOption;
import dev.JustRed23.jdautils.help.CommandHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommandHelpTest {

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

    JDABuilder createInstance() {
        return JDABuilder.createDefault(secrets.getProperty("token"))
                .setActivity(Activity.playing("with cats"))
                .setStatus(OnlineStatus.IDLE);
    }

    @Test
    void testHelpCommand() throws InterruptedException {
        JDAUtilities.createSlashCommand("help", "Shows a list of all commands or help for a specific command")
                .addOption(new CommandOption(OptionType.STRING, "command", "The command to get help for", false))
                .executes(event -> {
                    final OptionMapping command = event.getOption("command");
                    EmbedBuilder embedToSend = (command == null ? CommandHelp.getAllCommands() : CommandHelp.getCommandHelp(command.getAsString()));

                    if (!embedToSend.getFields().isEmpty())
                        event.replyEmbeds(embedToSend.build()).queue();
                    else
                        event.reply("No command found with that name").setEphemeral(true)
                                .queue(hook -> hook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
                })
                .buildAndRegister();

        JDAUtilities.createSlashCommand("testone", "A test command to check if the help command works")
                .addOption(new CommandOption(OptionType.STRING, "test", "A test option", true))
                .buildAndRegister();

        JDAUtilities.createSlashCommand("testtwo", "A random test description")
                .buildAndRegister();

        JDA instance = createInstance()
                .addEventListeners(JDAUtilities.getInstance().listener())
                .build().awaitReady();

        assertNotNull(instance);

        Thread.sleep(10000);

        instance.shutdown();
    }
}
