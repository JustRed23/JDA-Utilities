package dev.JustRed23.jdautils.command;

import dev.JustRed23.jdautils.event.EventWatcher;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class SlashCommand {

    public static SlashCommandBuilder slash(String name, String description) {
        return new SlashCommandBuilder(name, description);
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
            data.addOption(option.type(), option.name(), option.description(), option.required(), option.autocomplete());
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
}
