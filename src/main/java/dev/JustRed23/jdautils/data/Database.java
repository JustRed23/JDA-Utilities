package dev.JustRed23.jdautils.data;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

class Database {

    private static final List<Manager> managers = new ArrayList<>();

    static {
        Reflections reflections = new Reflections(Database.class.getPackage().getName() + ".impl");
        reflections.getSubTypesOf(Manager.class).forEach(manager -> {
            try {
                managers.add(manager.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("An error occurred while trying to register default managers", e);
            }
        });
    }

    static Manager get(Class<? extends Manager> clazz) {
        return managers.stream().filter(manager -> manager.getClass().equals(clazz)).findFirst().orElseThrow(() -> new IllegalArgumentException("No manager found for class " + clazz.getName()));
    }
}
