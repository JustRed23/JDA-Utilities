import dev.JustRed23.jdautils.command.Command;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandTest {

    @Test
    void testSlashCommand() {
        SlashCommandData basic = Command.slash("test", "a simple test command")
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
}
