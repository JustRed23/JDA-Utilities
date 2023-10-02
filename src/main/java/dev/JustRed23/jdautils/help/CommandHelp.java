package dev.JustRed23.jdautils.help;

import dev.JustRed23.jdautils.JDAUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * A class to help with the creation of help commands for slash commands
 */
public final class CommandHelp {

    private static EmbedBuilder DEFAULT_BUILDER = new EmbedBuilder()
            .setColor(0x00ff00)
            .setFooter("Help provided by JDA-Utilities", JDAUtilities.getBotIconUrl());

    /**
     * Gets help for a specific command
     * @param commandName The name of the command to get help for
     * @return An EmbedBuilder containing the help for the command, or empty if the command was not found
     * @see EmbedBuilder#isEmpty()
     * @see #setDefaultBuilder(EmbedBuilder)
     */
    public static EmbedBuilder getCommandHelp(String commandName) {
        Checks.notEmpty(commandName, "Command name");
        EmbedBuilder builder = makeCommandBuilder();
        getCommandHelp(commandName, builder);
        return builder;
    }

    /**
     * Gets help for a specific command
     * @param commandName The name of the command to get help for
     * @param builder The EmbedBuilder to use for the help <br><b>NOTE - The content of the builder will be overridden</b>
     * @see EmbedBuilder#isEmpty()
     */
    public static void getCommandHelp(String commandName, @NotNull EmbedBuilder builder) {
        builder.clearFields();
        getCommands().filter(cmd -> cmd.getName().equalsIgnoreCase(commandName))
                .findFirst()
                .ifPresent(cmd -> {
                    builder.setTitle(cmd.getName());
                    builder.setDescription(cmd.getDescription());

                    if (cmd.getOptions().isEmpty()) return;
                    builder.addField("", "__**Options**__ *(\\* = required)*", false);
                    cmd.getOptions().forEach(option -> builder.addField(option.getName() + (option.isRequired() ? "*" : ""), option.getDescription(), false));
                });
    }

    /**
     * Gets help for all commands
     * @return An EmbedBuilder containing the names and descriptions of all commands
     * @see #setDefaultBuilder(EmbedBuilder)
     */
    public static EmbedBuilder getAllCommands() {
        EmbedBuilder builder = makeCommandBuilder();
        builder.setTitle("Showing list of all commands");
        getAllCommands(builder);
        return builder;
    }

    /**
     * Gets help for all commands
     * @param builder The EmbedBuilder to use for the help <br><b>NOTE - The content of the builder will be overridden</b>
     * @see #setDefaultBuilder(EmbedBuilder)
     */
    public static void getAllCommands(@NotNull EmbedBuilder builder) {
        if (getCommands().findAny().isEmpty()) {
            builder.setDescription("No commands found");
            return;
        }

        builder.clearFields();
        getCommands().forEach(cmd -> builder.addField(cmd.getName(), cmd.getDescription(), false));
    }

    /**
     * Sets the default EmbedBuilder to use for help commands, this can be used to set the color, footer, etc.
     * @param builder The EmbedBuilder to use
     */
    public static void setDefaultBuilder(@NotNull EmbedBuilder builder) {
        Checks.notNull(builder, "EmbedBuilder");
        DEFAULT_BUILDER = builder;
    }

    private static Stream<SlashCommandData> getCommands() {
        return dev.JustRed23.jdautils.command.Command.globalCommands
                .stream()
                .filter(cmd -> cmd instanceof SlashCommandData)
                .map(cmd -> (SlashCommandData) cmd);
    }

    private static EmbedBuilder makeCommandBuilder() {
        return new EmbedBuilder(DEFAULT_BUILDER);
    }
}
