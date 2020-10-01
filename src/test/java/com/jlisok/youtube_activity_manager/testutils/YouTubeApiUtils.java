package com.jlisok.youtube_activity_manager.testutils;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoSnippet;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.utils.VideoCreator;
import com.jlisok.youtube_activity_manager.youtube.utils.VideoDescription;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class YouTubeApiUtils {

    private final static URI uri = URI.create("https://www.example.com");
    private final static Random random = new Random();

    public static List<com.google.api.services.youtube.model.Video> createRandomYouTubeVideoList(int size) {
        if (size == 0) {
            return new ArrayList<>(0);
        }
        return IntStream.range(0, size).mapToObj(i -> {
            VideoContentDetails details = createRandomVideoContentDetails();
            VideoSnippet snippet = createRandomVideoSnippet();
            return new com.google.api.services.youtube.model.Video()
                    .setContentDetails(details)
                    .setSnippet(snippet)
                    .setId(UUID.randomUUID().toString());
        }).collect(Collectors.toList());
    }


    public static String createDescriptionWithRandomUriNumber() {
        return IntStream.range(0, random.nextInt(10))
                        .mapToObj(i -> uri.toString())
                        .collect(Collectors.joining(" "));
    }


    public static void assertListOfVideosEqual(List<com.google.api.services.youtube.model.Video> youTubeList, List<Video> actualVideoList) {
        assertEquals(youTubeList.size(), actualVideoList.size());
        for (int i = 0; i < actualVideoList.size(); i++) {
            assertEquals(youTubeList.get(i).getSnippet().getChannelId(), actualVideoList.get(i).getChannelId());
            assertEquals(youTubeList.get(i).getSnippet().getTitle(), actualVideoList.get(i).getTitle());
            assertEquals(youTubeList.get(i).getContentDetails().getDuration(), actualVideoList.get(i).getDuration().toString());
            assertEquals(youTubeList.get(i).getSnippet().getTags(), actualVideoList.get(i).getHashtag());
        }
    }


    public static void assertVideoNotEmpty(Video video) {
        assertFalse(video.getChannelId().isEmpty());
        assertNotNull(video.getHashtag());
        assertNotNull(video.getUri());
        assertFalse(video.getTitle().isEmpty());
        assertFalse(video.getId().toString().isEmpty());
    }

    public static List<Video> createRandomListOfVideos(int size) {
        return IntStream.range(0, size)
                        .mapToObj(i -> createRandomVideo())
                        .collect(Collectors.toList());
    }

    public static Video createRandomVideo() {
        String videoId = createRandomString();
        List<String> uriList = VideoDescription.toListOfUri(createDescriptionWithRandomUriNumber());
        return VideoCreator.createVideo(videoId, createRandomVideoSnippet(), createRandomVideoContentDetails(), uriList);
    }


    private static VideoContentDetails createRandomVideoContentDetails() {
        return new VideoContentDetails()
                .setDuration(Duration.ofMinutes(15).toString());
    }


    private static VideoSnippet createRandomVideoSnippet() {
        return new VideoSnippet()
                .setChannelId(createRandomString())
                .setTags(createRandomTags())
                .setTitle(createRandomString())
                .setDescription(createDescriptionWithRandomUriNumber())
                .setPublishedAt(new DateTime(Instant.now().toEpochMilli()));
    }

    private static String createRandomString() {
        return RandomStringUtils.randomAlphanumeric(20);
    }


    private static List<String> createRandomTags() {
        return IntStream.range(0, random.nextInt(10))
                        .mapToObj(i -> createRandomString())
                        .collect(Collectors.toList());
    }
}
