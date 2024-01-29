package dev.JustRed23.jdautils.command;

import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.event.EventWatcher;
import dev.JustRed23.jdautils.utils.Unique;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class Command {

    public static final List<CommandData> globalCommands = new ArrayList<>();

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

    interface Buildable<R, T extends CommandData> extends GlobalModifiers<R, T> {
        /**
         * Builds and creates an event watcher for the command, and returns the command data.<br>
         * <b>You will still need to register the command with JDA</b>
         * @return the command data
         */
        T build();

        /**
         * Builds the command and automatically registers it with JDA.<br>
         * <b>Make sure to call this before actually starting a JDA instance, as commands will be registered in the {@link ReadyEvent}</b>
         */
        default void buildAndRegister() {
            if (JDAUtilities.getInstance().isReady())
                throw new IllegalStateException("Cannot register commands after JDA has been started, please register commands before starting JDA");

            T data = build();
            globalCommands.add(data);
        }
    }

    @SuppressWarnings("unchecked")
    interface GlobalModifiers<R, T extends CommandData> {
        @ApiStatus.Internal
        T getData();

        default R modifyData(@NotNull Function<T, T> function) {
            function.apply(getData());
            return (R) this;
        }

        default R setDefaultPermissions(DefaultMemberPermissions defaultPermissions) {
            getData().setDefaultPermissions(defaultPermissions);
            return (R) this;
        }

        default R setNSFW(boolean nsfw) {
            getData().setNSFW(nsfw);
            return (R) this;
        }

        default R setGuildOnly() {
            getData().setGuildOnly(true);
            return (R) this;
        }
    }

    public static class SlashCommandBuilder implements Buildable<SlashCommandBuilder, SlashCommandData> {

        private final SlashCommandData data;

        private final Map<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> autocompleteListeners = new HashMap<>();
        private final List<Function<SlashCommandInteractionEvent, Boolean>> conditions = new ArrayList<>();
        private EventWatcher.Listener<SlashCommandInteractionEvent> listener;

        //Sub commands
        private final Map<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> subCommandAutoCompleteListeners = new HashMap<>();
        private final Map<String, List<Function<SlashCommandInteractionEvent, Boolean>>> subCommandConditions = new HashMap<>();
        private final Map<String, EventWatcher.Listener<SlashCommandInteractionEvent>> subCommandListeners = new HashMap<>();
        private boolean containsSubCommands;

        private SlashCommandBuilder(String name, String description) {
            data = Commands.slash(name, description);
        }

        public SlashCommandBuilder addOption(@NotNull CommandOption option) {
            if (containsSubCommands)
                throw new IllegalStateException("Cannot add an option to a slash command that contains sub commands");

            OptionData optionData = new OptionData(option.type(), option.name(), option.description(), option.required(), option.autocomplete());
            for (net.dv8tion.jda.api.interactions.commands.Command.Choice choice : option.choices())
                optionData.addChoice(choice.getName(), choice.getAsString());
            getData().addOptions(optionData);

            if (option.autocomplete() && option.autoCompleteListener() != null)
                autocompleteListeners.put(option.name(), option.autoCompleteListener());

            return this;
        }

        public SlashCommandBuilder addOptions(@NotNull CommandOption... options) {
            if (containsSubCommands)
                throw new IllegalStateException("Cannot add an option to a slash command that contains sub commands");

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

        public SlashCommandBuilder addCondition(Function<SlashCommandInteractionEvent, Boolean> condition) {
            if (containsSubCommands)
                throw new IllegalStateException("Cannot add a condition to a slash command that contains sub commands");
            this.conditions.add(condition);
            return this;
        }

        public SubCommandBuilder addSubCommand(String name, String description) {
            if (listener != null)
                throw new IllegalStateException("Cannot add a sub command to a slash command that contains a listener");
            containsSubCommands = true;
            return new SubCommandBuilder(this, name, description);
        }

        public SlashCommandData getData() {
            return data;
        }

        public SlashCommandData build() {
            if (!containsSubCommands && listener != null) {
                new EventWatcher<>(new CommandComponent(getData().getName()), SlashCommandInteractionEvent.class)
                        .setListener(listener)
                        .addConditions(conditions);

                for (Map.Entry<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> entry : autocompleteListeners.entrySet())
                    new EventWatcher<>(new OptionComponent(getData().getName() + " " + entry.getKey()), CommandAutoCompleteInteractionEvent.class)
                            .setListener(entry.getValue());
            } else {
                for (Map.Entry<String, EventWatcher.Listener<SlashCommandInteractionEvent>> entry : subCommandListeners.entrySet())
                    new EventWatcher<>(new CommandComponent(getData().getName() + " " + entry.getKey()), SlashCommandInteractionEvent.class)
                            .setListener(entry.getValue())
                            .addConditions(subCommandConditions.getOrDefault(entry.getKey(), Collections.emptyList()));

                for (Map.Entry<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> entry : subCommandAutoCompleteListeners.entrySet())
                    new EventWatcher<>(new OptionComponent(getData().getName() + " " + entry.getKey()), CommandAutoCompleteInteractionEvent.class)
                            .setListener(entry.getValue());
            }
            return getData();
        }

        public static class SubCommandBuilder {

            private final SlashCommandBuilder parent;
            private final SubcommandData data;
            private final Map<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> autocompleteListeners = new HashMap<>();
            private final List<Function<SlashCommandInteractionEvent, Boolean>> conditions = new ArrayList<>();
            private EventWatcher.Listener<SlashCommandInteractionEvent> listener;

            private SubCommandBuilder(SlashCommandBuilder parent, String name, String description) {
                this.parent = parent;
                data = new SubcommandData(name, description);
            }

            public SubCommandBuilder addOption(@NotNull CommandOption option) {
                data.addOption(option.type(), option.name(), option.description(), option.required(), option.autocomplete());

                if (option.autocomplete() && option.autoCompleteListener() != null)
                    autocompleteListeners.put(option.name(), option.autoCompleteListener());

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

            public SubCommandBuilder addCondition(Function<SlashCommandInteractionEvent, Boolean> condition) {
                this.conditions.add(condition);
                return this;
            }

            public SubCommandBuilder modifyData(@NotNull Function<SubcommandData, SubcommandData> function) {
                function.apply(data);
                return this;
            }

            public SlashCommandBuilder build() {
                parent.getData().addSubcommands(data);
                if (listener != null) {
                    parent.subCommandListeners.put(data.getName(), listener);

                    if (!conditions.isEmpty())
                        parent.subCommandConditions.put(data.getName(), conditions);

                    for (Map.Entry<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> entry : autocompleteListeners.entrySet())
                        parent.subCommandAutoCompleteListeners.put(data.getName() + " " + entry.getKey(), entry.getValue());
                }
                return parent;
            }
        }
    }

    public static class MessageContextBuilder implements Buildable<MessageContextBuilder, CommandData> {

        private final CommandData data;
        private final List<Function<MessageContextInteractionEvent, Boolean>> conditions = new ArrayList<>();
        private EventWatcher.Listener<MessageContextInteractionEvent> listener;

        private MessageContextBuilder(String name) {
            this.data = Commands.message(name);
        }

        public MessageContextBuilder executes(EventWatcher.Listener<MessageContextInteractionEvent> listener) {
            this.listener = listener;
            return this;
        }

        public MessageContextBuilder addCondition(Function<MessageContextInteractionEvent, Boolean> condition) {
            this.conditions.add(condition);
            return this;
        }

        public CommandData getData() {
            return data;
        }

        public CommandData build() {
            if (listener != null) {
                new EventWatcher<>(new CommandComponent(getData().getName()).setContextCommand(true), MessageContextInteractionEvent.class)
                        .setListener(listener)
                        .addConditions(conditions);
            }
            return getData();
        }
    }

    public static class UserContextBuilder implements Buildable<UserContextBuilder, CommandData> {

        private final CommandData data;
        private final List<Function<UserContextInteractionEvent, Boolean>> conditions = new ArrayList<>();
        private EventWatcher.Listener<UserContextInteractionEvent> listener;

        private UserContextBuilder(String name) {
            this.data = Commands.user(name);
        }

        public UserContextBuilder executes(EventWatcher.Listener<UserContextInteractionEvent> listener) {
            this.listener = listener;
            return this;
        }

        public UserContextBuilder addCondition(Function<UserContextInteractionEvent, Boolean> condition) {
            this.conditions.add(condition);
            return this;
        }

        public CommandData getData() {
            return data;
        }

        public CommandData build() {
            if (listener != null) {
                new EventWatcher<>(new CommandComponent(getData().getName()).setContextCommand(true), UserContextInteractionEvent.class)
                        .setListener(listener)
                        .addConditions(conditions);
            }
            return getData();
        }
    }
}
