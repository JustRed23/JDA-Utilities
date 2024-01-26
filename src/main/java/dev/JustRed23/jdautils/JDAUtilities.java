package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.command.Command;
import dev.JustRed23.jdautils.component.SendableComponent;
import dev.JustRed23.jdautils.event.EventWatcher;
import dev.JustRed23.jdautils.message.MessageComponent;
import dev.JustRed23.jdautils.message.MessageFilter;
import dev.JustRed23.jdautils.music.AudioManager;
import dev.JustRed23.jdautils.settings.GuildSettingManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This is the main class of JDAUtilities, this is used to initialize JDAUtilities and get some useful methods
 * <p>This class is meant as a utility class, the methods in this class <b>CANNOT</b> be used before JDAUtilities has been initialized</p>
 * <p>The methods in this class directly reference static methods in other classes, this is done to make it easier to use. <b>This does not mean that you can call these static methods directly as we use an internal event listener to handle everything</b></p>
 * <p>To initialize JDA Utilities you need to call {@link #getInstance()} and add the {@link Builder#listener()} to your JDA instance</p>
 * <p>Example:</p>
 * <pre><code>
 * JDA jda = JDABuilder.createDefault("token").build();
 * jda.addEventListener(JDAUtilities.getInstance().listener());
 * jda.awaitReady();
 * // JDAUtilities is now initialized
 * </code></pre>
 */
public final class JDAUtilities {

    private static final String version = "1.5";
    static Builder builder;

    private JDAUtilities() {}

    /* INTERNAL */
    private static void checkInitialized() {
        if (builder == null)
            throw new IllegalStateException("JDAUtilities has not been initialized");

        if (!builder.ready)
            throw new IllegalStateException("JDAUtilities has not been initialized, please add the listener to your JDA instance");
    }
    /* INTERNAL */

    /**
     * Gets or creates an instance of the builder, this is used to initialize JDAUtilities
     * @see Builder
     */
    public static Builder getInstance() {
        if (builder == null)
            builder = new Builder();
        return builder;
    }

    /**
     * Gets the guild setting manager, this is used to get and set guild settings
     * @see Builder#withGuildSettingManager(GuildSettingManager)
     */
    public static @Nullable GuildSettingManager getGuildSettingManager() {
        checkInitialized();
        return builder.guildSettingManager;
    }

    /**
     * Gets the guild filter manager, this is used to get and set guild filters
     * @param guild The guild to get the filter manager for
     * @return The filter manager for the specified guild
     * @see MessageFilter#get(Guild)
     */
    public static @NotNull MessageFilter getGuildFilterManager(@NotNull Guild guild) {
        checkInitialized();
        return MessageFilter.get(guild);
    }

    /**
     * Adds a listener to the guild message listener, this fires when a message is received in the specified guild
     * @param guild The guild to listen for messages in
     * @param listener The listener to add
     * @return The event watcher that you can use to remove the listener
     */
    public static @NotNull EventWatcher<MessageReceivedEvent> addGuildMessageListener(Guild guild, EventWatcher.Listener<MessageReceivedEvent> listener) {
        return addMessageListener(listener, event -> event.getGuild().equals(guild));
    }

    /**
     * Adds a listener to the message listener, this fires when a message is received in any guild
     * @param listener The listener to add
     * @param condition The condition to check before the listener is called
     * @return The event watcher that you can use to remove the listener
     */
    public static @NotNull EventWatcher<MessageReceivedEvent> addMessageListener(EventWatcher.Listener<MessageReceivedEvent> listener, Function<MessageReceivedEvent, Boolean> condition) {
        checkInitialized();
        return new EventWatcher<>(new MessageComponent(listener), MessageReceivedEvent.class)
                .setListener(listener)
                .addCondition(condition);
    }

    /**
     * Gets the audio manager for the specified guild, this is used to control the music player
     * @param guild The guild to get the audio manager for
     * @return The audio manager for the specified guild
     * @see AudioManager#get(Guild)
     */
    public static @NotNull AudioManager getGuildAudioManager(@NotNull Guild guild) {
        checkInitialized();
        return AudioManager.get(guild);
    }

    /**
     * Creates a new sendable component
     * @param clazz The class to create the component from
     * @return An instance of the specified class, used to send components
     * @see SendableComponent#create(Class)
     */
    public static @Nullable SendableComponent createComponent(Class<? extends SendableComponent> clazz) {
        checkInitialized();
        return SendableComponent.create(clazz);
    }

    /**
     * Creates a new sendable component
     * @param clazz The class to create the component from
     * @param paramTypes The types of the arguments to pass to the constructor
     * @param constructorArgs The arguments to pass to the constructor
     * @return An instance of the specified class, used to send components
     * @see SendableComponent#create(Class, Class[], Object...)
     */
    public static @Nullable SendableComponent createComponent(Class<? extends SendableComponent> clazz, Class<?>[] paramTypes, Object... constructorArgs) {
        checkInitialized();
        if (paramTypes.length != constructorArgs.length)
            throw new IllegalArgumentException("The amount of parameter types does not match the amount of constructor arguments");
        return SendableComponent.create(clazz, paramTypes, constructorArgs);
    }

    /**
     * Creates a new slash command
     * @param name The name of the command
     * @param description The description of the command
     * @return A builder to create the command
     * @see Command#slash(String, String)
     */
    public static @NotNull Command.SlashCommandBuilder createSlashCommand(@NotNull String name, @NotNull String description) {
        return Command.slash(name, description);
    }

    /**
     * Creates a new message context command, these are commands that you can execute when a user right-clicks on a message
     * @param name The name of the command
     * @return A builder to create the command
     * @see Command#message(String)
     */
    public static @NotNull Command.MessageContextBuilder createMessageContextCommand(@NotNull String name) {
        return Command.message(name);
    }

    /**
     * Creates a new user context command, these are commands that you can execute when a user right-clicks on a user
     * @param name The name of the command
     * @return A builder to create the command
     * @see Command#user(String)
     */
    public static @NotNull Command.UserContextBuilder createUserContextCommand(@NotNull String name) {
        return Command.user(name);
    }

    /**
     * @return The effective avatar url of the bot
     */
    public static String getBotIconUrl() {
        checkInitialized();
        return builder.cachedBotIconUrl;
    }

    public static String getVersion() {
        return version;
    }

    @Contract(pure = true)
    public static @NotNull String getAuthor() {
        return "JustRed23";
    }

    @Contract(pure = true)
    public static @NotNull String getGithub() {
        return "https://github.com/JustRed23/JDA-Utilities";
    }
}
