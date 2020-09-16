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

import static org.junit.jupiter.api.Assertions.*;

public class YouTubeApiUtils {

    private final static URI uri = URI.create("https://www.example.com");
    private final static Random random = new Random();

    public static List<com.google.api.services.youtube.model.Video> createRandomYouTubeVideoList(int loopEnd) {
        if (loopEnd == 0) {
            return new ArrayList<>(0);
        }
        List<com.google.api.services.youtube.model.Video> videoList = new ArrayList<>(loopEnd);
        for (int i = 0; i < loopEnd; i++) {
            VideoContentDetails details = createRandomVideoContentDetails();
            VideoSnippet snippet = createRandomVideoSnippet();
            com.google.api.services.youtube.model.Video video = new com.google.api.services.youtube.model.Video()
                    .setContentDetails(details)
                    .setSnippet(snippet)
                    .setId(UUID
                                   .randomUUID()
                                   .toString());

            videoList.add(video);
        }
        return videoList;
    }


    public static String createDescriptionWithRandomUriNumber() {
        String string = "";
        for (int i = 0; i < random.nextInt(10); i++) {
            string = string.concat(uri.toString() + " ");
        }
        return string;
    }


    public static void assertListOfVideosEqual(List<com.google.api.services.youtube.model.Video> youTubeList, List<Video> actualVideoList) {
        assertEquals(youTubeList.size(), actualVideoList.size());
        for (int i = 0; i < actualVideoList.size(); i++) {
            assertEquals(youTubeList
                                 .get(i)
                                 .getSnippet()
                                 .getChannelId(), actualVideoList
                                 .get(i)
                                 .getChannelId());
            assertEquals(youTubeList
                                 .get(i)
                                 .getSnippet()
                                 .getTitle(), actualVideoList
                                 .get(i)
                                 .getTitle());
            assertEquals(youTubeList
                                 .get(i)
                                 .getContentDetails()
                                 .getDuration(), actualVideoList
                                 .get(i)
                                 .getDuration()
                                 .toString());
            assertEquals(youTubeList
                                 .get(i)
                                 .getSnippet()
                                 .getTags(), actualVideoList
                                 .get(i)
                                 .getHashtag());
        }
    }


    public static void assertVideoNotEmpty(Video video) {
        assertFalse(video.getChannelId().isEmpty());
        assertNotNull(video.getHashtag());
        assertNotNull(video.getUri());
        assertFalse(video.getTitle().isEmpty());
        assertFalse(video.getId().toString().isEmpty());
    }

    public static List<Video> createRandomListOfVideos(int loopEnd) {
        List<Video> videoList = new ArrayList<>(loopEnd);
        for (int i = 0; i < loopEnd; i++) {
            Video video = createRandomVideo();
            videoList.add(video);
        }
        return videoList;
    }

    public static Video createRandomVideo() {
        String videoId = createRandomString();
        List<String> uriList = VideoDescription.toListOfUri(createDescriptionWithRandomUriNumber());
        return VideoCreator.createVideo(videoId, createRandomVideoSnippet(), createRandomVideoContentDetails(), uriList);
    }


    private static VideoContentDetails createRandomVideoContentDetails() {
        return new VideoContentDetails()
                .setDuration(Duration
                                     .ofMinutes(15)
                                     .toString());
    }


    private static VideoSnippet createRandomVideoSnippet() {
        return new VideoSnippet()
                .setChannelId(createRandomString())
                .setTags(createRandomTags())
                .setTitle(createRandomString())
                .setDescription(createDescriptionWithRandomUriNumber())
                .setPublishedAt(new DateTime(Instant
                                                     .now()
                                                     .toEpochMilli()));
    }

    private static String createRandomString() {
        return RandomStringUtils.randomAlphanumeric(20);
    }


    private static List<String> createRandomTags() {
        List<String> hashtags = new ArrayList<>();
        for (int i = 0; i < random.nextInt(10); i++) {
            hashtags.add(createRandomString());
        }
        return hashtags;
    }


}
