package dev.JustRed23.jdautils.music.search;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import dev.JustRed23.jdautils.music.AudioSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class YouTubeSource {

    @Contract(pure = true)
    public static @NotNull String getThumbnail(String videoID) {
        return "http://img.youtube.com/vi/" + videoID +"/0.jpg";
    }

    @Contract(pure = true)
    public static @NotNull String getVideo(String videoID) {
        return "https://www.youtube.com/watch?v=" + videoID;
    }

    @Contract(pure = true)
    public static @NotNull String getVideoID(@NotNull String youtubeUrl) {
        return youtubeUrl.replace("https://www.youtube.com/watch?v=", "");
    }

    public static @NotNull YouTubeSource login(@NotNull String appName, @NotNull String token) throws GeneralSecurityException, IOException {
        YouTubeSource youTubeSource = new YouTubeSource(token);
        youTubeSource.builder.setApplicationName(appName);
        youTubeSource.youtube = youTubeSource.builder.build();
        return youTubeSource;
    }

    private final YouTube.Builder builder;
    private final String token;
    private YouTube youtube;

    private YouTubeSource(String token) throws GeneralSecurityException, IOException {
        this.builder = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), null);
        this.token = token;
    }

    /**
     * Search for a video on YouTube, returns the first 5 results
     * @param query The query to search for
     * @return A list of search results, limited to 5
     * @throws IOException If the request fails
     */
    public List<SearchResult> search(String query) throws IOException {
        return search(query, 5);
    }

    /**
     * Search for a video on YouTube, returns the first x results
     * @param query The query to search for
     * @param limit The amount of results to return
     * @return A list of search results, limited to x
     * @throws IOException If the request fails
     */
    public List<SearchResult> search(String query, long limit) throws IOException {
        List<SearchResult> results = youtube.search()
                .list(Collections.singletonList("id,snippet"))
                .setQ(query)
                .setMaxResults(limit)
                .setType(Collections.singletonList("video"))
                .setFields("items(id(kind,videoId),snippet(title,channelId,channelTitle,thumbnails/default/url))")
                .setKey(token)
                .execute()
                .getItems();

        return results.isEmpty() ? null : results;
    }

    /**
     * Get the details of (multiple) videos
     * @param videoIDs The video ID(s) to get the details of
     * @return A list of video details
     * @throws IOException If the request fails
     */
    public List<VideoContentDetails> getVideoDetails(String... videoIDs) throws IOException {
        List<Video> details = youtube.videos()
                .list(Collections.singletonList("contentDetails"))
                .setId(List.of(videoIDs))
                .setKey(token)
                .execute()
                .getItems();

        return details.isEmpty() ? null : details.stream().map(Video::getContentDetails).toList();
    }

    public AudioSource getSource() {
        return AudioSource.YOUTUBE;
    }
}
