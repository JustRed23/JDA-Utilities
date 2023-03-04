package dev.JustRed23.jdautils.music.search;

import dev.JustRed23.jdautils.music.AudioSource;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SpotifySource {

    public static @NotNull SpotifySource login(@NotNull String clientId, @NotNull String clientSecret) throws IOException, ParseException, SpotifyWebApiException {
        return new SpotifySource(clientId, clientSecret);
    }

    private final SpotifyApi api;
    private long expiresSeconds;

    private SpotifySource(String clientId, String clientSecret) throws IOException, ParseException, SpotifyWebApiException {
        api = SpotifyApi.builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
        tryLogin(api);
    }

    private void tryLogin(@NotNull SpotifyApi api) throws IOException, ParseException, SpotifyWebApiException {
        final ClientCredentials execute = api.clientCredentials().build().execute();
        api.setAccessToken(execute.getAccessToken());
        LoggerFactory.getLogger(SpotifySource.class).info("Successfully logged in to Spotify, token expires in " + execute.getExpiresIn() + " seconds");
        expiresSeconds = System.currentTimeMillis() + (execute.getExpiresIn() * 1000);
    }

    /**
     * Returns the Spotify API instance, will automatically try to authenticate if the token has expired
     * @return The Spotify API instance, with a valid token
     */
    public @NotNull SpotifyApi getApi() throws IOException, ParseException, SpotifyWebApiException {
        if (System.currentTimeMillis() > expiresSeconds)
            tryLogin(api);
        return api;
    }

    /**
     * Searches for a track on Spotify and return the first 5 results
     * @param query The query to search for
     * @return A list of tracks, limited to 5 results
     * @throws IOException When a network error occurs
     * @throws ParseException When a parse error occurs
     * @throws SpotifyWebApiException When the API returns an invalid response
     */
    public List<Track> search(String query) throws IOException, ParseException, SpotifyWebApiException {
        return search(query, 5);
    }

    /**
     * Searches for a track on Spotify and return the first X results
     * @param query The query to search for
     * @param limit The amount of results to return
     * @return A list of tracks, limited to X results
     * @throws IOException When a network error occurs
     * @throws ParseException When a parse error occurs
     * @throws SpotifyWebApiException When the API returns an invalid response
     */
    public List<Track> search(String query, int limit) throws IOException, ParseException, SpotifyWebApiException {
        final Paging<Track> execute = getApi().searchTracks(query).limit(limit).build().execute();
        return Arrays.stream(execute.getItems()).toList();
    }

    public AudioSource getSource() {
        return AudioSource.SPOTIFY;
    }
}
