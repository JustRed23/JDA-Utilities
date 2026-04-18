package dev.JustRed23.jdautils.music;

/**
 * Defines how tracks should be repeated during playback.
 * <p>
 * This enum controls the repeat behavior of the music player, determining
 * whether tracks are played once, repeated individually, or cycled through repeatedly.
 */
public enum RepeatMode {
    /**
     * No repeating - each track plays once, then moves to the next.
     */
    OFF,

    /**
     * Repeat the current track indefinitely until manually stopped or skipped.
     */
    ONE,

    /**
     * Repeat the entire queue - when the last track finishes, start over from the beginning.
     */
    ALL;

    /**
     * Parses a string identifier into a RepeatMode enum value.
     * <p>
     * Accepts case-insensitive identifiers: "off", "one", "all".
     *
     * @param mode The string identifier to parse.
     * @return The corresponding RepeatMode.
     * @throws IllegalArgumentException if the mode string is not recognized.
     */
    public static RepeatMode get(String mode) {
        switch (mode.toLowerCase()) {
            case "off":
                return OFF;
            case "one":
                return ONE;
            case "all":
                return ALL;
            default:
                throw new IllegalArgumentException("Invalid repeat mode: " + mode);
        }
    }
}
