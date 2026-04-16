package dev.JustRed23.jdautils.music;

public enum RepeatMode {
    OFF,
    ONE,
    ALL;

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
