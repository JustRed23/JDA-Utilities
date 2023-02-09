package dev.JustRed23.jdautils;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class JDAUtilities {

    private static String version;

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

    public static Builder newInstance() {
        return new Builder();
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
