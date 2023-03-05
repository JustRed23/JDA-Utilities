package dev.JustRed23.jdautils.command;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.List;

public class CommandOption {

    private final OptionType type;
    private final String name;
    private final String description;
    private final boolean required;
    private final boolean autocomplete;
    private final List<Command.Choice> choices = new ArrayList<>();

    public CommandOption(OptionType type, String name, String description, boolean required, boolean autocomplete) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
        this.autocomplete = autocomplete;
    }

    public CommandOption(OptionType type, String name, String description, boolean required) {
        this(type, name, description, required, false);
    }

    public CommandOption(OptionType type, String name, String description) {
        this(type, name, description, false);
    }

    public CommandOption addChoice(Command.Choice choice) {
        choices.add(choice);
        return this;
    }

    public CommandOption addChoice(String name, String value) {
        choices.add(new Command.Choice(name, value));
        return this;
    }

    public OptionType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public boolean required() {
        return required;
    }

    public boolean autocomplete() {
        return autocomplete;
    }

    public List<Command.Choice> choices() {
        return choices;
    }
}
