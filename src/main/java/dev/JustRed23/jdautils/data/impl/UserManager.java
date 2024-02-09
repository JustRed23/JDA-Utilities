package dev.JustRed23.jdautils.data.impl;

import dev.JustRed23.jdautils.data.Manager;

public class UserManager extends Manager {

    public UserManager() {
        super("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, setting VARCHAR(255) UNIQUE NOT NULL, value VARCHAR(255) NOT NULL)", "user_%s_settings");
    }
}
