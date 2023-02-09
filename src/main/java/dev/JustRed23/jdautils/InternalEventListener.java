package dev.JustRed23.jdautils;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.JustRed23.jdautils.JDAUtilities.*;

public final class InternalEventListener extends ListenerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(InternalEventListener.class);

    InternalEventListener() {}

    public void onReady(@NotNull ReadyEvent ignored) {
        LOGGER.info("JDA Utilities v{} by {}", getVersion(), getAuthor());
        LOGGER.info("Github: {}", getGithub());
    }
}
