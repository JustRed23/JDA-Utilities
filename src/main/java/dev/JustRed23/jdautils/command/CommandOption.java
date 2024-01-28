package dev.JustRed23.jdautils.command;

import dev.JustRed23.jdautils.event.EventWatcher;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandOption {

    private final OptionType type;
    private final String name;
    private final String description;
    private final boolean required;
    private final boolean autocomplete;
    private final List<Command.Choice> choices = new ArrayList<>();
    private EventWatcher.Listener<CommandAutoCompleteInteractionEvent> autoCompleteListener;

    public CommandOption(OptionType type, String name, String description, boolean required, boolean autocomplete) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
        this.autocomplete = autocomplete;

        if (autocomplete && !type.canSupportChoices())
            throw new IllegalStateException("This option type does not support auto completions");
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

    public CommandOption onAutoComplete(EventWatcher.Listener<CommandAutoCompleteInteractionEvent> listener) {
        if (!autocomplete)
            throw new IllegalStateException("Cannot add an autocomplete listener to a non-autocomplete option");
        autoCompleteListener = listener;
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

    public @Nullable EventWatcher.Listener<CommandAutoCompleteInteractionEvent> autoCompleteListener() {
        return autoCompleteListener;
    }
}
