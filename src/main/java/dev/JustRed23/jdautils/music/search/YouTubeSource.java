package dev.JustRed23.jdautils.music.search;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import dev.JustRed23.jdautils.music.PlayableTrack;
import dev.JustRed23.jdautils.music.TrackSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class YouTubeSource {

    @Contract(pure = true)
    public static @NotNull String getThumbnail(String videoID) {
        return "https://img.youtube.com/vi/" + videoID + "/0.jpg";
    }

    @Contract(pure = true)
    public static @NotNull String getVideo(String videoID) {
        return "https://www.youtube.com/watch?v=" + videoID;
    }

    @Contract(pure = true)
    public static @NotNull String getVideoId(@NotNull String youtubeUrl) {
        String value = youtubeUrl.trim();

        if (value.contains("youtu.be/")) {
            value = value.substring(value.lastIndexOf('/') + 1);
        } else if (value.contains("watch?v=")) {
            value = value.substring(value.indexOf("watch?v=") + 8);
        } else if (value.contains("/shorts/")) {
            value = value.substring(value.indexOf("/shorts/") + 8);
        } else if (value.contains("/embed/")) {
            value = value.substring(value.indexOf("/embed/") + 7);
        }

        int queryIndex = value.indexOf('?');
        if (queryIndex >= 0) {
            value = value.substring(0, queryIndex);
        }

        int ampIndex = value.indexOf('&');
        if (ampIndex >= 0) {
            value = value.substring(0, ampIndex);
        }

        return value;
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

    public @NotNull YouTube getApi() {
        return youtube;
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

        return results == null ? List.of() : results;
    }

    public @NotNull List<PlayableTrack> searchTracks(String query) throws IOException {
        return searchTracks(query, 5);
    }

    public @NotNull List<PlayableTrack> searchTracks(String query, long limit) throws IOException {
        List<SearchResult> results = search(query, limit);
        if (results.isEmpty()) {
            return List.of();
        }

        Map<String, Long> durations = new HashMap<>();
        List<String> videoIds = results.stream()
                .map(result -> result.getId() == null ? null : result.getId().getVideoId())
                .filter(id -> id != null && !id.isBlank())
                .toList();

        if (!videoIds.isEmpty()) {
            List<VideoContentDetails> details = getVideoDetails(videoIds.toArray(new String[0]));
            for (int i = 0; i < Math.min(videoIds.size(), details.size()); i++) {
                VideoContentDetails detail = details.get(i);
                if (detail != null && detail.getDuration() != null) {
                    durations.put(videoIds.get(i), Duration.parse(detail.getDuration()).toMillis());
                }
            }
        }

        return results.stream()
                .map(result -> toPlayableTrack(result, durations))
                .toList();
    }

    public @NotNull PlayableTrack toPlayableTrack(@NotNull SearchResult result) {
        return toPlayableTrack(result, Collections.emptyMap());
    }

    private @NotNull PlayableTrack toPlayableTrack(@NotNull SearchResult result, @NotNull Map<String, Long> durations) {
        String videoId = result.getId() == null ? null : result.getId().getVideoId();
        long duration = videoId == null ? -1L : durations.getOrDefault(videoId, -1L);
        String title = result.getSnippet() == null ? "Unknown title" : result.getSnippet().getTitle();
        String author = result.getSnippet() == null ? null : result.getSnippet().getChannelTitle();
        String thumbnail = result.getSnippet() == null || result.getSnippet().getThumbnails() == null || result.getSnippet().getThumbnails().getDefault() == null ? null : result.getSnippet().getThumbnails().getDefault().getUrl();

        return new PlayableTrack(
                TrackSource.YOUTUBE,
                videoId,
                title == null ? "Unknown title" : title,
                videoId == null || videoId.isBlank() ? "https://www.youtube.com" : getVideo(videoId),
                thumbnail == null || thumbnail.isBlank() ? null : thumbnail,
                author,
                null,
                duration,
                result
        );
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

        return details == null ? List.of() : details.stream().map(Video::getContentDetails).toList();
    }
}
