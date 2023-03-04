import com.google.api.services.youtube.model.SearchResult;
import dev.JustRed23.jdautils.music.search.Search;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SourceTest {

    private static Properties secrets;

    @BeforeAll
    static void getProperties() {
        try (InputStream secretsFile = BaseTest.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            secrets = new Properties();
            secrets.load(secretsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Search.setAppName("JDA-Utilities TEST");
    }

    @Test
    void testYoutube() throws GeneralSecurityException, IOException {
        final List<SearchResult> search = Search.YouTube(secrets.getProperty("youtube-token"))
                .search("rick astley never gonna give you up");
        assertNotNull(search);
        assertFalse(search.isEmpty());

        search.forEach(result -> {
            System.out.println();
            System.out.println("RESULT - " + result.getSnippet().getTitle());
            System.out.println("Channel: " + result.getSnippet().getChannelTitle() + " (" + result.getSnippet().getChannelId() + ")");
            System.out.println("Thumbnail: " + result.getSnippet().getThumbnails().getDefault().getUrl());
        });
    }

    @Test
    void testSpotify() throws IOException, ParseException, SpotifyWebApiException {
        final List<Track> search = Search.Spotify(secrets.getProperty("spotify-id"), secrets.getProperty("spotify-secret"))
                .search("rick astley never gonna give you up");
        assertNotNull(search);
        assertFalse(search.isEmpty());

        search.forEach(result -> {
            System.out.println();
            System.out.println("RESULT - " + result.getName());
            System.out.println("Artist: " + result.getArtists()[0].getName() + " (" + result.getArtists()[0].getId() + ")");
            System.out.println("Album: " + result.getAlbum().getName() + " (" + result.getAlbum().getId() + ")");
            System.out.println("Thumbnail: " + result.getAlbum().getImages()[0].getUrl());
        });
    }
}
