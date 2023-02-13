package dev.JustRed23.jdautils.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public record CommandOption(OptionType type, String name, String description, boolean required, boolean autocomplete) {

    public CommandOption(OptionType type, String name, String description, boolean required) {
        this(type, name, description, required, false);
    }

    public CommandOption(OptionType type, String name, String description) {
        this(type, name, description, false);
    }
}
