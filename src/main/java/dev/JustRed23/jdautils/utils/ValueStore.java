package dev.JustRed23.jdautils.utils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple key-value store that saves to a YAML file
 */
public final class ValueStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValueStore.class);

    private final String fileName;
    private final Object writeLock = new Object();

    private Map<String, String> values = new ConcurrentHashMap<>();

    /**
     * Loads or creates a new ValueStore from the given file name (without the .yml extension)
     * @param fileName the file name, without the extension
     * @return a ValueStore instance
     */
    public static @NotNull ValueStore loadOrCreate(String fileName) {
        return loadOrCreate(fileName, null);
    }

    /**
     * Loads or creates a new ValueStore from the given file name (without the .yml extension) and directory
     * @param fileName the file name, without the extension
     * @param parentDir the parent directory of the file, or null if the file should be saved in the current working directory
     * @return a ValueStore instance
     */
    public static @NotNull ValueStore loadOrCreate(String fileName, File parentDir) {
        if (fileName.endsWith(".yml"))
            fileName = fileName.substring(0, fileName.length() - 4);

        File file;

        if (parentDir != null) {
            parentDir.mkdirs();
            file = new File(parentDir, fileName + ".yml");
        } else file = new File(fileName + ".yml");

        ValueStore store = new ValueStore(fileName);

        if (file.exists()) {
            try (FileReader read = new FileReader(fileName + ".yml")) {
                store.values = new ConcurrentHashMap<>(new Yaml().load(read));
            } catch (IOException e) {
                LOGGER.error("Failed to load values from {}.yml", fileName, e);
            }
        }

        store.save();
        return store;
    }

    private ValueStore(String fileName) {
        this.fileName = fileName;
    }

    public void put(String key, String value) {
        values.put(key, value);
        save();
    }

    public String get(String key) {
        return values.get(key);
    }

    /**
     * Saves the current values to the file
     */
    public void save() {
        synchronized (writeLock) {
            try (FileWriter wr = new FileWriter(fileName + ".yml")) {
                new Yaml().dump(values, wr);
            } catch (IOException e) {
                LOGGER.error("Failed to save values to {}.yml", fileName, e);
            }
        }
    }

    /**
     * Prints all values to the console
     */
    public void print() {
        LOGGER.info("Values in {}:", fileName);
        values.forEach((key, value) -> LOGGER.info("{}: {}", key, value));
    }
}
