package dev.JustRed23.jdautils;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.SendableComponent;
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

    public static SendableComponent createComponent(Class<? extends Component> clazz) {
        checkInitialized();

        Class<? extends SendableComponent> component = builder.sendableComponentRegistry.getComponents()
                .keySet()
                .stream()
                .filter(it -> it.equals(clazz))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Component with name " + clazz.getSimpleName() + " does not exist"));

        return builder.sendableComponentRegistry.create(component);
    }

    public static SendableComponent createComponent(String componentName) {
        checkInitialized();

        Class<? extends SendableComponent> component = builder.sendableComponentRegistry.getComponents()
                .entrySet()
                .stream()
                .filter(it -> it.getValue().equals(componentName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Component with name " + componentName + " does not exist"))
                .getKey();

        return builder.sendableComponentRegistry.create(component);
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
