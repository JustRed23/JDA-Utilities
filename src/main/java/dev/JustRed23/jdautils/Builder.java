package dev.JustRed23.jdautils;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public final class Builder {

    Builder() {}

    public ListenerAdapter build() {
        return new InternalEventListener();
    }
}
