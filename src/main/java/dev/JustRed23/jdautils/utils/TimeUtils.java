package dev.JustRed23.jdautils.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private TimeUtils() {}

    /**
     * Converts milliseconds to displayable time
     * @param millis milliseconds
     * @return displayable time (ex. 01:00:00 as 1 hour)
     */
    @NotNull
    public static String millisToTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        String time = "";

        if (days > 0)
            time += String.format("%02d:", days);

        if (hours > 0)
            time += String.format("%02d:", hours);

        time += String.format("%02d:%02d", minutes, seconds);

        return time;
    }

    /**
     * Converts displayable time to milliseconds
     * @param time displayable time (ex. 01:00:00 as 1 hour)
     * @return the time in milliseconds
     */
    public static long timeToMillis(@NotNull String time) {
        int days, hours, minutes = 0, seconds = 0;

        String[] t = time.split(":");
        long ms = 0;

        if (t.length == 4) {
            days = Integer.parseInt(t[0]);
            hours = Integer.parseInt(t[1]);
            minutes = Integer.parseInt(t[2]);
            seconds = Integer.parseInt(t[3]);

            if (days < 0)
                throw new IllegalArgumentException("Days must be more than -1");

            ms += TimeUnit.DAYS.toMillis(days);

            if (hours < 0 || hours > 24)
                throw new IllegalArgumentException("Hours must be more than -1 and less than 24");

            ms += TimeUnit.HOURS.toMillis(hours);
        } else if (t.length == 3) {
            hours = Integer.parseInt(t[0]);
            minutes = Integer.parseInt(t[1]);
            seconds = Integer.parseInt(t[2]);

            if (hours < 0 || hours > 24)
                throw new IllegalArgumentException("Hours must be more than -1 and less than 24");

            ms += TimeUnit.HOURS.toMillis(hours);
        } else if (t.length == 2) {
            minutes = Integer.parseInt(t[0]);
            seconds = Integer.parseInt(t[1]);
        }

        if (minutes < 0 || minutes > 60)
            throw new IllegalArgumentException("Minutes must be more than -1 and less than 60");

        if (seconds < 0 || seconds > 60)
            throw new IllegalArgumentException("Seconds must be more than -1 and less than 60");

        ms += TimeUnit.MINUTES.toMillis(minutes);
        ms += TimeUnit.SECONDS.toMillis(seconds);

        return ms;
    }

    /**
     * Converts a youtube time to a string
     * @param ytTime The time in youtube format
     * @return The time in string format
     * @see <a href="https://developers.google.com/youtube/v3/docs/videos#contentDetails.duration">Youtube time format</a>
     * @see #millisToTime(long)
     */
    @NotNull
    public static String youtubeTime(String ytTime) {
        return millisToTime(Duration.parse(ytTime).toMillis());
    }
}
