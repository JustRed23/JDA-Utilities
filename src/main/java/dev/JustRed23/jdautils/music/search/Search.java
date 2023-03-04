package dev.JustRed23.jdautils.music.search;

import dev.JustRed23.jdautils.JDAUtilities;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * This class is a common entry point for all search sources. You can also use the specific sources directly. <br>
 * <br>
 * Available sources: <br>
 * - {@link SpotifySource} <br>
 * - {@link YouTubeSource} <br>
 */
public final class Search {

    private static String appName = JDAUtilities.getVersion() + " v" + JDAUtilities.getVersion();

    private Search() {}

    /**
     * Sets the application name, this is used for some sources to identify the application.
     * @param appName The application name to use (default: JDAUtilities v(VERSION))
     */
    public static void setAppName(String appName) {
        Search.appName = appName;
    }

    public static @NotNull SpotifySource Spotify(String clientId, String clientSecret) throws IOException, ParseException, SpotifyWebApiException {
        return SpotifySource.login(clientId, clientSecret);
    }

    public static @NotNull YouTubeSource YouTube(String token) throws GeneralSecurityException, IOException {
        return YouTubeSource.login(appName, token);
    }
}
