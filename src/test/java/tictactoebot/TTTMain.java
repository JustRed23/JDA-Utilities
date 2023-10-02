package tictactoebot;

import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.command.CommandOption;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import testapp.Main;

import java.io.InputStream;
import java.util.Properties;

public class TTTMain {

    public static void main(String[] args) throws InterruptedException {
        Properties secrets = null;
        try (InputStream secretsFile = Main.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            secrets = new Properties();
            secrets.load(secretsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JDAUtilities.createSlashCommand("tictactoe", "Play a game of Tic Tac Toe")
                .addSubCommand("start", "Start a game of Tic Tac Toe")
                    .addOption(new CommandOption(OptionType.USER, "opponent", "The opponent to play against", true))
                    .executes(event -> {
                        User opponent = event.getOption("opponent").getAsUser();
                        if (opponent.isBot()) {
                            event.reply("You can't play against a bot!").setEphemeral(true).queue();
                            return;
                        }

                        if (opponent.equals(event.getMember().getUser())) {
                            event.reply("You can't play against yourself!").setEphemeral(true).queue();
                            return;
                        }

                        JDAUtilities.createComponent(TTTInviteComponent.class, new Class[] {User.class, User.class}, event.getMember().getUser(), opponent).reply(event);
                    })
                    .build()
                .modifyData(data -> data.setGuildOnly(true))
                .buildAndRegister();

        JDABuilder.createDefault(secrets.getProperty("token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("with cats"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(JDAUtilities.getInstance().listener())
                .build().awaitReady();
    }
}
