package dev.JustRed23.jdautils.music;

import org.jetbrains.annotations.Nullable;

public enum AudioSource {
    YOUTUBE("youtube", "https://www.youtube.com/"),
    SOUNDCLOUD("soundcloud", "https://soundcloud.com/"),
    BANDCAMP("bandcamp", "https://bandcamp.com/"),
    VIMEO("vimeo", "https://vimeo.com/"),
    TWITCH("twitch", "https://www.twitch.tv/"),
    BEAM("beam", "https://beam.pro/"),
    GETYARN("getyarn", "https://getyarn.io/"),
    HTTP("http"),
    UNKNOWN("unknown");

    private final String name;
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

    public @Nullable String getUrl() {
        return url;
    }

    /**
     * Get the AudioSource by name
     * @param name The name of the AudioSource
     * @return The AudioSource, if not found it will return {@link #UNKNOWN}
     */
    public static AudioSource getByName(String name) {
        for (AudioSource source : values()) {
            if (source.getName().equalsIgnoreCase(name))
                return source;
        }
        return UNKNOWN;
    }
}
