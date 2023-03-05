package dev.JustRed23.jdautils.command;

import dev.JustRed23.jdautils.event.EventWatcher;
import dev.JustRed23.jdautils.utils.Unique;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class Command {

    @NotNull
    @Contract("_, _ -> new")
    public static SlashCommandBuilder slash(@NotNull String name, @NotNull String description) {
        Checks.notEmpty(name, "Name");
        Checks.notEmpty(description, "Description");
        Unique.checkUnique("slashcommand", name, "A slash command with the name '" + name + "' already exists");
        return new SlashCommandBuilder(name, description);
    }

    @NotNull
    @Contract("_ -> new")
    public static MessageContextBuilder message(@NotNull String name) {
        Checks.notEmpty(name, "Name");
        Unique.checkUnique("message context", name, "A message context command with the name '" + name + "' already exists");
        return new MessageContextBuilder(name);
    }

    @NotNull
    @Contract("_ -> new")
    public static UserContextBuilder user(@NotNull String name) {
        Checks.notEmpty(name, "Name");
        Unique.checkUnique("message context", name, "A user context command with the name '" + name + "' already exists");
        return new UserContextBuilder(name);
    }

    public static class SlashCommandBuilder {

        private final SlashCommandData data;
        private EventWatcher.Listener<SlashCommandInteractionEvent> listener;
        private final Map<String, EventWatcher.Listener<SlashCommandInteractionEvent>> subCommandListeners = new HashMap<>();
        private boolean containsSubCommands;

        private SlashCommandBuilder(String name, String description) {
            data = Commands.slash(name, description);
        }

        public SlashCommandBuilder addOption(@NotNull CommandOption option) {
            OptionData optionData = new OptionData(option.type(), option.name(), option.description(), option.required(), option.autocomplete());
            for (net.dv8tion.jda.api.interactions.commands.Command.Choice choice : option.choices())
                optionData.addChoice(choice.getName(), choice.getAsString());
            data.addOptions(optionData);
            return this;
        }

        public SlashCommandBuilder addOptions(@NotNull CommandOption... options) {
            for (CommandOption option : options)
                addOption(option);
            return this;
        }

        public SlashCommandBuilder executes(EventWatcher.Listener<SlashCommandInteractionEvent> listener) {
            if (containsSubCommands)
                throw new IllegalStateException("Cannot add a listener to a slash command that contains sub commands");
            this.listener = listener;
            return this;
        }

        public SubCommandBuilder addSubCommand(String name, String description) {
            if (listener != null)
                throw new IllegalStateException("Cannot add a sub command to a slash command that contains a listener");
            containsSubCommands = true;
            return new SubCommandBuilder(this, name, description);
        }

        public SlashCommandBuilder modifyData(@NotNull Function<SlashCommandData, SlashCommandData> function) {
            function.apply(data);
            return this;
        }

        public SlashCommandData build() {
            if (listener != null)
                new EventWatcher(new CommandComponent(data.getName()), SlashCommandInteractionEvent.class).setListener(listener);
            else {
                for (Map.Entry<String, EventWatcher.Listener<SlashCommandInteractionEvent>> entry : subCommandListeners.entrySet())
                    new EventWatcher(new CommandComponent(data.getName() + " " + entry.getKey()), SlashCommandInteractionEvent.class).setListener(entry.getValue());
            }
            return data;
        }

        public static class SubCommandBuilder {

            private final SlashCommandBuilder parent;
            private final SubcommandData data;
            private EventWatcher.Listener<SlashCommandInteractionEvent> listener;

            private SubCommandBuilder(SlashCommandBuilder parent, String name, String description) {
                this.parent = parent;
                data = new SubcommandData(name, description);
            }

            public SubCommandBuilder addOption(@NotNull CommandOption option) {
                data.addOption(option.type(), option.name(), option.description(), option.required(), option.autocomplete());
                return this;
            }

            public SubCommandBuilder addOptions(@NotNull CommandOption... options) {
                for (CommandOption option : options)
                    addOption(option);
                return this;
            }

            public SubCommandBuilder executes(EventWatcher.Listener<SlashCommandInteractionEvent> listener) {
                if (this.listener != null)
                    throw new IllegalStateException("Cannot add a listener to a sub command that already has a listener");
                this.listener = listener;
                return this;
            }

            public SubCommandBuilder modifyData(@NotNull Function<SubcommandData, SubcommandData> function) {
                function.apply(data);
                return this;
            }

            public SlashCommandBuilder build() {
                parent.data.addSubcommands(data);
                parent.subCommandListeners.put(data.getName(), listener);
                return parent;
            }
        }
    }

    public static class MessageContextBuilder {

        private final CommandData data;
        private EventWatcher.Listener<MessageContextInteractionEvent> listener;

        private MessageContextBuilder(String name) {
            this.data = Commands.message(name);
        }

        public MessageContextBuilder executes(EventWatcher.Listener<MessageContextInteractionEvent> listener) {
            this.listener = listener;
            return this;
        }

        public MessageContextBuilder modifyData(@NotNull Function<CommandData, CommandData> function) {
            function.apply(data);
            return this;
        }

        public CommandData build() {
            if (listener != null)
                new EventWatcher(new CommandComponent(data.getName()).setContextCommand(true), MessageContextInteractionEvent.class).setListener(listener);
            return data;
        }
    }

    public static class UserContextBuilder {

        private final CommandData data;
        private EventWatcher.Listener<UserContextInteractionEvent> listener;

        private UserContextBuilder(String name) {
            this.data = Commands.user(name);
        }

        public UserContextBuilder executes(EventWatcher.Listener<UserContextInteractionEvent> listener) {
            this.listener = listener;
            return this;
        }

        public UserContextBuilder modifyData(@NotNull Function<CommandData, CommandData> function) {
            function.apply(data);
            return this;
        }

        public CommandData build() {
            if (listener != null)
                new EventWatcher(new CommandComponent(data.getName()).setContextCommand(true), UserContextInteractionEvent.class).setListener(listener);
            return data;
        }
    }
}
