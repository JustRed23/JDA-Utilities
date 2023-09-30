package dev.JustRed23.jdautils.help;

import dev.JustRed23.jdautils.JDAUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public final class CommandHelp {

    private static EmbedBuilder DEFAULT_BUILDER = new EmbedBuilder()
            .setColor(0x00ff00)
            .setFooter("Help provided by JDA-Utilities", JDAUtilities.getBotIconUrl());


    public static EmbedBuilder getCommandHelp(String commandName) {
        Checks.notEmpty(commandName, "Command name");
        EmbedBuilder builder = makeCommandBuilder();
        getCommandHelp(commandName, builder);
        return builder;
    }

    public static void getCommandHelp(String commandName, @NotNull EmbedBuilder builder) {
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

    public static EmbedBuilder getAllCommands() {
        EmbedBuilder builder = makeCommandBuilder();
        builder.setTitle("Showing list of all commands");
        getAllCommands(builder);
        return builder;
    }

    public static void getAllCommands(@NotNull EmbedBuilder builder) {
        if (getCommands().findAny().isEmpty()) {
            builder.setDescription("No commands found");
            return;
        }

        getCommands().forEach(cmd -> builder.addField(cmd.getName(), cmd.getDescription(), false));
    }

    private static Stream<SlashCommandData> getCommands() {
        return dev.JustRed23.jdautils.command.Command.globalCommands
                .stream()
                .filter(cmd -> cmd instanceof SlashCommandData)
                .map(cmd -> (SlashCommandData) cmd);
    }

    public static void setDefaultBuilder(@NotNull EmbedBuilder builder) {
        Checks.notNull(builder, "EmbedBuilder");
        DEFAULT_BUILDER = builder;
    }

    private static EmbedBuilder makeCommandBuilder() {
        return new EmbedBuilder(DEFAULT_BUILDER);
    }
}
