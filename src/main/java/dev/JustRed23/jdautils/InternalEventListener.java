package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.command.Command;
import dev.JustRed23.jdautils.component.SendableComponent;
import dev.JustRed23.jdautils.event.WatcherManager;
import dev.JustRed23.jdautils.message.Filter;
import dev.JustRed23.jdautils.message.MessageFilter;
import dev.JustRed23.jdautils.music.AudioManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateVoiceStatusEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateAvatarEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static dev.JustRed23.jdautils.JDAUtilities.*;

final class InternalEventListener extends ListenerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(InternalEventListener.class);
    private final Builder builder;

    InternalEventListener(Builder builder) {
        this.builder = builder;
    }

    //GENERIC EVENTS
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("--------------------------------------------------");
        LOGGER.info("JDA Utilities v{} by {}", getVersion(), getAuthor());
        LOGGER.info("Github: {}", getGithub());
        LOGGER.info("--------------------------------------------------");
        builder.cachedBotIconUrl = event.getJDA().getSelfUser().getEffectiveAvatarUrl(); //Cache the bots avatar url
        builder.ready();

        if (builder.guildSettingManager != null)
            builder.guildSettingManager.loadGuilds(event.getJDA().getGuilds());

        event.getJDA()
                .updateCommands()
                .addCommands(Command.globalCommands)
                .queue(success -> LOGGER.info("Successfully registered {} global command(s)", Command.globalCommands.size()),
                        failure -> LOGGER.error("Failed to register global command(s): {}", failure.getMessage()));
    }

    public void onGenericEvent(@NotNull GenericEvent event) {
        WatcherManager.cleanup();
    }

    public void onStatusChange(@NotNull StatusChangeEvent event) {
        if (event.getNewStatus().equals(JDA.Status.SHUTTING_DOWN)) {
            LOGGER.info("Shutting down JDA Utilities");
            builder.destroy();
        }
    }

    public void onSelfUpdateAvatar(@NotNull SelfUpdateAvatarEvent event) {
        builder.cachedBotIconUrl = event.getNewAvatarUrl();
    }
    //GENERIC EVENTS

    //GUILD EVENTS
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        if (builder.guildSettingManager != null)
            builder.guildSettingManager.addGuild(event.getGuild().getIdLong());
    }

    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        if (builder.guildSettingManager != null)
            builder.guildSettingManager.removeGuild(event.getGuild().getIdLong());

        if (AudioManager.has(event.getGuild()))
            AudioManager.get(event.getGuild()).destroy();
    }

    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        boolean isBotAffected = event.getEntity().equals(event.getGuild().getSelfMember());

        if (isBotAffected && AudioManager.has(event.getGuild())) {
            if (event.getChannelJoined() == null) //Bot left and was not moved
                AudioManager.get(event.getGuild()).disconnect();
        }
    }

    public void onChannelUpdateVoiceStatus(@NotNull ChannelUpdateVoiceStatusEvent event) {
        if (!event.getChannel().getType().isAudio() || !AudioManager.has(event.getGuild())) return;
        final VoiceChannel connectedChannel = event.getChannel().asVoiceChannel();

        final AudioManager audioManager = AudioManager.get(event.getGuild());
        if (!audioManager.isConnected() || audioManager.getScheduler().isStatusChangedManually()) return;

        if (audioManager.getConnectedChannel().equals(connectedChannel)) {
            if (audioManager.getScheduler().isCurrentStatus(event.getNewValue()))
                return;

            audioManager.getScheduler().channelStatusChangedManually();
        }
    }

    //GUILD EVENTS

    //INTERACTION EVENTS
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        WatcherManager.onInteractionEvent(event.getComponentId(), event);
    }

    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        WatcherManager.onInteractionEvent(event.getComponentId(), event);
    }

    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        WatcherManager.onInteractionEvent(event.getComponentId(), event);
    }

    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        WatcherManager.onInteractionEvent(event.getModalId(), event);
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        WatcherManager.onCommandEvent(event);
    }

    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        WatcherManager.onContextEvent(event);
    }

    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        WatcherManager.onContextEvent(event);
    }
    //INTERACTION EVENTS

    //MESSAGE EVENTS
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage())
            return;

        if (event.isFromGuild()) { //Fire the filter only if the message is from a guild
            List<Filter> triggeredFilters = MessageFilter.broadcastEvent(event);

            if (!triggeredFilters.isEmpty()){
                WatcherManager.onFilterTrigger(triggeredFilters, event);
                if (!event.getMessage().getType().isSystem())
                    event.getMessage().delete().queue();
                return;
            }
        }

        WatcherManager.onMessageEvent(event);
    }

    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        WatcherManager.onReactionEvent(event);
    }

    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        WatcherManager.onReactionEvent(event);
    }

    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        if (!event.isFromGuild())
            return;

        List<SendableComponent> toRemove = new ArrayList<>();
        SendableComponent.getInstances()
                .stream()
                .filter(SendableComponent::isSent)
                .filter(component -> event.getGuild().equals(component.getGuild()))
                .filter(component -> component.getMessageId() == event.getMessageIdLong())
                .forEach(component -> {
                    component.remove();
                    toRemove.add(component);
                }
        );
        SendableComponent.getInstances().removeAll(toRemove);

        WatcherManager.cleanup(event);
    }
    //MESSAGE EVENTS
}
