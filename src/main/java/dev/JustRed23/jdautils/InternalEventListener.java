package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.component.Component;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static dev.JustRed23.jdautils.JDAUtilities.*;

public final class InternalEventListener extends ListenerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(InternalEventListener.class);
    private final Builder builder;

    InternalEventListener(Builder builder) {
        this.builder = builder;
    }

    public void onReady(@NotNull ReadyEvent ignored) {
        LOGGER.info("--------------------------------------------------");
        LOGGER.info("JDA Utilities v{} by {}", getVersion(), getAuthor());
        LOGGER.info("Github: {}", getGithub());
        LOGGER.info("--------------------------------------------------");
        builder.freezeRegistries();
    }

    public void onStatusChange(@NotNull StatusChangeEvent event) {
        if (event.getNewStatus().equals(JDA.Status.SHUTTING_DOWN)) {
            LOGGER.info("Shutting down JDA Utilities");
            builder.destroy();
        }
    }

    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        if (!event.isFromGuild())
            return;

        List<Component> toRemove = new ArrayList<>();
        builder.componentRegistry.getInstances()
                .stream()
                .filter(Component::isSent)
                .filter(component -> event.getGuild().equals(component.getGuild()))
                .filter(component -> component.getMessageId() == event.getMessageIdLong())
                .forEach(component -> {
                    component.remove();
                    toRemove.add(component);
                }
        );
        builder.componentRegistry.getInstances().removeAll(toRemove);
    }
}
