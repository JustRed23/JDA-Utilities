package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.settings.GuildSettingManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public final class Builder {

    boolean ready = false;
    private final ListenerAdapter adapter;
    GuildSettingManager guildSettingManager;

    Builder() {
        adapter = new InternalEventListener(this);
    }

    /* INTERNAL */
    void destroy() {
        if (guildSettingManager != null)
            guildSettingManager.shutdown();

        JDAUtilities.builder = null;
    }
    /* INTERNAL */

    public Builder withGuildSettingManager(GuildSettingManager guildSettingManager) {
        if (ready)
            throw new IllegalStateException("JDA Utilities is already ready, you must call this method before JDA initialization");

        this.guildSettingManager = guildSettingManager;
        return this;
    }

    public ListenerAdapter listener() {
        return adapter;
    }
}
