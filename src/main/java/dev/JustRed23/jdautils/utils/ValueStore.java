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

public final class ValueStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValueStore.class);

    private final String fileName;
    private final Object writeLock = new Object();

    private Map<String, String> values = new ConcurrentHashMap<>();

    public static @NotNull ValueStore loadOrCreate(String fileName) {
        File file = new File(fileName + ".yml");

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

    public void save() {
        synchronized (writeLock) {
            try (FileWriter wr = new FileWriter(fileName + ".yml")) {
                new Yaml().dump(values, wr);
            } catch (IOException e) {
                LOGGER.error("Failed to save values to {}.yml", fileName, e);
            }
        }
    }

    public void print() {
        LOGGER.info("Values in {}:", fileName);
        values.forEach((key, value) -> LOGGER.info("{}: {}", key, value));
    }
}
