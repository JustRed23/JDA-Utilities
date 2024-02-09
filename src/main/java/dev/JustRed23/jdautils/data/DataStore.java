package dev.JustRed23.jdautils.data;

import dev.JustRed23.jdautils.data.impl.CustomManager;
import dev.JustRed23.jdautils.data.impl.GuildManager;
import dev.JustRed23.jdautils.data.impl.UserManager;

public enum DataStore {

    GUILD(GuildManager.class),
    USER(UserManager.class),
    CUSTOM(CustomManager.class);

    private final Class<? extends Manager> clazz;

    DataStore(Class<? extends Manager> clazz) {
        this.clazz = clazz;
    }

    public Manager use() {
        return Database.get(clazz);
    }
}
