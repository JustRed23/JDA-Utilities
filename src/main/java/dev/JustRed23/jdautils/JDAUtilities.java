package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.component.SendableComponent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class JDAUtilities {

    private static String version;
    static Builder builder;

    static {
        try (InputStream properties = JDAUtilities.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            prop.load(properties);
            version = prop.getProperty("version");
        } catch (IOException e) {
            LoggerFactory.getLogger(JDAUtilities.class).error("Failed to load application.properties", e);
            version = "unknown";
        }
    }

    private JDAUtilities() {}

    /* INTERNAL */
    private static void checkInitialized() {
        if (builder == null)
            throw new IllegalStateException("JDAUtilities has not been initialized");

        if (!builder.ready)
            throw new IllegalStateException("JDAUtilities has not been initialized, please add the listener to your JDA instance");
    }
    /* INTERNAL */

    public static Builder getInstance() {
        if (builder == null)
            builder = new Builder();
        return builder;
    }

    public static @Nullable SendableComponent createComponent(Class<? extends SendableComponent> clazz) {
        checkInitialized();
        return builder.sendableComponentRegistry.create(clazz);
    }

    public static @Nullable SendableComponent createComponent(String componentName) {
        checkInitialized();
        return builder.sendableComponentRegistry.create(componentName);
    }

    public static String getVersion() {
        return version;
    }

    public static String getAuthor() {
        return "JustRed23";
    }

    public static String getGithub() {
        return "https://github.com/JustRed23/JDA-Utilities";
    }
}
