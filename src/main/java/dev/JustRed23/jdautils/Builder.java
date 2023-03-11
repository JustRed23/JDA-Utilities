package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.message.MessageFilter;
import dev.JustRed23.jdautils.music.AudioManager;
import dev.JustRed23.jdautils.music.effect.AbstractEffect;
import dev.JustRed23.jdautils.settings.DefaultGuildSettingManager;
import dev.JustRed23.jdautils.settings.GuildSettingManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;

public final class Builder {

    boolean ready = false;
    private final ListenerAdapter adapter;
    GuildSettingManager guildSettingManager;

    Builder() {
        adapter = new InternalEventListener(this);
    }

    /* INTERNAL */
    void ready() {
        Reflections reflections = new Reflections(AbstractEffect.class.getPackageName() + ".impl");
        reflections.getSubTypesOf(AbstractEffect.class).forEach(clazz -> {
            try {
                clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while trying to register default effects", e);
            }
        });

        ready = true;
    }

    void destroy() {
        if (guildSettingManager != null)
            guildSettingManager.shutdown();

        AudioManager.destroyAll();
        MessageFilter.destroyAll();

        JDAUtilities.builder = null;
    }
    /* INTERNAL */

    /**
     * Sets the guild setting manager, if you do not want to create your own you can create a new {@link DefaultGuildSettingManager} instance
     * @param guildSettingManager The guild setting manager to use
     * @throws IllegalStateException If JDA Utilities has already been initialized
     */
    public Builder withGuildSettingManager(GuildSettingManager guildSettingManager) {
        if (ready)
            throw new IllegalStateException("JDA Utilities is already ready, you must call this method before JDA initialization");

        this.guildSettingManager = guildSettingManager;
        return this;
    }

    /**
     * Gets the internal event listener, you need to add this to your JDA instance
     * @see JDA#addEventListener(Object...)
     */
    public ListenerAdapter listener() {
        return adapter;
    }
}
