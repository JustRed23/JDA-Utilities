package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.Nullable;

public enum AudioSource {
    SPOTIFY("spotify", "https://spotify.com/"),
    YOUTUBE("youtube", "https://youtube.com/"),
    UNKNOWN("unknown");

    private String name;
    private String url;

    AudioSource(String name, String url) {
        this.name = name;
        this.url = url;
    }

    AudioSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public AudioSource withName(String name) {
        this.name = name;
        return this;
    }

    public @Nullable String getUrl() {
        return url;
    }

    /**
     * Get the AudioSource by name
     * @param name The name of the AudioSource
     * @return The AudioSource, if not found it will return {@link #UNKNOWN} with the name set to the given name
     * @see #getName()
     */
    public static AudioSource getByName(String name) {
        for (AudioSource source : values()) {
            if (source.getName().equalsIgnoreCase(name))
                return source;
        }
        return UNKNOWN.withName(name);
    }
}
