package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.message.MessageFilter;
import dev.JustRed23.jdautils.music.AudioManager;
import dev.JustRed23.jdautils.music.effect.AbstractEffect;
import dev.JustRed23.jdautils.settings.DefaultGuildSettingManager;
import dev.JustRed23.jdautils.settings.GuildSettingManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.reflections.Reflections;

public final class Builder {

    boolean ready = false;
    private final ListenerAdapter adapter;

    String cachedBotIconUrl;

    Builder() {
        org.sqlite.JDBC.isValidURL(org.sqlite.JDBC.PREFIX); //Call a static JDBC method, this forces the JDBC driver to be loaded
        adapter = new InternalEventListener(this);
    }

    @ApiStatus.Internal
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

    @ApiStatus.Internal
    void destroy() {
        AudioManager.destroyAll();
        MessageFilter.destroyAll();

        JDAUtilities.builder = null;
    }

    /**
     * Gets the internal event listener, you need to add this to your JDA instance
     * @see JDA#addEventListener(Object...)
     */
    public ListenerAdapter listener() {
        return adapter;
    }

    /**
     * Indicates that JDA has finished loading and JDA Utilities is ready to be used
     * @return Whether JDA Utilities is ready
     */
    public boolean isReady() {
        return ready;
    }
}
