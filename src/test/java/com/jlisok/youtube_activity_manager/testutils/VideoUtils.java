package com.jlisok.youtube_activity_manager.testutils;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.*;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import com.jlisok.youtube_activity_manager.youtube.utils.VideoDescription;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jlisok.youtube_activity_manager.testutils.ChannelAndSubscriptionUtils.createRandomChannel;

public class VideoUtils {

    private final static URI uri = URI.create("https://www.example.com");
    private final static Random random = new Random();


    public static List<Video> createRandomYouTubeVideoList(int size, List<com.google.api.services.youtube.model.Channel> youTubeChannels) {
        if (size == 0) {
            return new ArrayList<>(0);
        }
        var youTubeChannelsSize = youTubeChannels.size();
        return IntStream.range(0, size)
                        .mapToObj(i -> {
                            var channelIndex = i % youTubeChannelsSize;
                            var channel = youTubeChannels.get(channelIndex);
                            VideoContentDetails details = createRandomVideoContentDetails();
                            VideoSnippet snippet = createRandomVideoSnippet();
                            snippet.setChannelId(channel.getId());
                            snippet.setCategoryId(createRandomString());
                            return new com.google.api.services.youtube.model.Video()
                                    .setContentDetails(details)
                                    .setSnippet(snippet)
                                    .setId(UUID.randomUUID().toString());
                        }).collect(Collectors.toList());
    }


    public static List<com.google.api.services.youtube.model.Video> createYouTubeVideoList(int size) {
        if (size == 0) {
            return new ArrayList<>(0);
        }
        List<com.google.api.services.youtube.model.Channel> youTubeChannels = ChannelAndSubscriptionUtils.createRandomYouTubeChannelList(size);
        List<com.google.api.services.youtube.model.VideoCategory> videoCategories = createRandomListOfYouTubeVideoCategories(size);
        return IntStream.range(0, size)
                        .mapToObj(i -> {
                            var channel = youTubeChannels.get(i);
                            var category = videoCategories.get(i);
                            VideoContentDetails details = createRandomVideoContentDetails();
                            VideoSnippet snippet = createRandomVideoSnippet();
                            snippet.setChannelId(channel.getId());
                            snippet.setCategoryId(category.getId());
                            return new com.google.api.services.youtube.model.Video()
                                    .setContentDetails(details)
                                    .setSnippet(snippet)
                                    .setId(UUID.randomUUID().toString());
                        }).collect(Collectors.toList());
    }


    public static List<com.google.api.services.youtube.model.Video> createYouTubeVideoListGivenChannelsAndVideoCategory(int size, List<com.google.api.services.youtube.model.Channel> youTubeChannels, List<VideoCategory> videoCategories) {
        if (size == 0) {
            return new ArrayList<>(0);
        }
        var youTubeChannelsSize = youTubeChannels.size();
        var videosSize = videoCategories.size();
        return IntStream.range(0, size)
                        .mapToObj(i -> {
                            var channelIndex = i % youTubeChannelsSize;
                            var channel = youTubeChannels.get(channelIndex);
                            var categoryIndex = i % videosSize;
                            var category = videoCategories.get(categoryIndex);
                            VideoContentDetails details = createRandomVideoContentDetails();
                            VideoSnippet snippet = createRandomVideoSnippet();
                            snippet.setChannelId(channel.getId());
                            snippet.setCategoryId(category.getYoutubeId());
                            return new com.google.api.services.youtube.model.Video()
                                    .setContentDetails(details)
                                    .setSnippet(snippet)
                                    .setId(UUID.randomUUID().toString());
                        }).collect(Collectors.toList());
    }


    public static List<com.jlisok.youtube_activity_manager.videos.models.Video> createListOfVideosFromYouTubeVideos(List<Video> youtubeVideos, List<Channel> channels, List<VideoCategory> videoCategories) {
        int channelSize = channels.size();
        int categoriesSize = videoCategories.size();
        return youtubeVideos
                .stream()
                .map(video -> EntityCreator
                        .createVideo(video.getId(),
                                     video.getSnippet(),
                                     video.getContentDetails(),
                                     Collections.singletonList("dwdqdqdqd"),
                                     channels.get(random.nextInt(channelSize)),
                                     videoCategories.get(random.nextInt(categoriesSize))))
                .collect(Collectors.toList());
    }


    public static List<com.jlisok.youtube_activity_manager.videos.models.Video> createRandomListOfVideos(int size, User user) {
        return IntStream.range(0, size)
                        .mapToObj(i -> createRandomVideo(user))
                        .collect(Collectors.toList());
    }


    public static VideoListResponse createVideoListResponse(List<com.google.api.services.youtube.model.Video> videos, String nextPageToken) {
        return new VideoListResponse()
                .setNextPageToken(nextPageToken)
                .setItems(videos);
    }


    public static com.jlisok.youtube_activity_manager.videos.models.Video createRandomVideo(User user) {
        String videoId = createRandomString();
        List<String> uriList = VideoDescription.toListOfUri(createDescriptionWithRandomUriNumber());
        Channel channel = createRandomChannel(user);
        VideoCategory videoCategory = createRandomVideoCategory();
        return EntityCreator.createVideo(videoId, createRandomVideoSnippet(), createRandomVideoContentDetails(), uriList, channel, videoCategory);
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


    public static String createDescriptionWithRandomUriNumber() {
        return IntStream.range(0, random.nextInt(10))
                        .mapToObj(i -> uri.toString())
                        .collect(Collectors.joining(" "));
    }


    public static List<com.google.api.services.youtube.model.VideoCategory> createRandomListOfYouTubeVideoCategories(int size) {
        return IntStream.range(0, size)
                        .mapToObj(i -> {
                            var videoCategorySnippet = new VideoCategorySnippet()
                                    .setAssignable(true)
                                    .setChannelId(createRandomString())
                                    .setTitle(createRandomString());
                            return new com.google.api.services.youtube.model.VideoCategory()
                                    .setEtag(createRandomString())
                                    .setId(createRandomString())
                                    .setKind(createRandomString())
                                    .setSnippet(videoCategorySnippet);
                        })
                        .collect(Collectors.toList());
    }


    public static List<com.google.api.services.youtube.model.VideoCategory> createRandomListOfYouTubeVideoCategoriesById(List<String> ids) {
        return ids.stream()
                  .map(id -> {
                      var videoCategorySnippet = new VideoCategorySnippet()
                              .setAssignable(true)
                              .setChannelId(createRandomString())
                              .setTitle(createRandomString());
                      return new com.google.api.services.youtube.model.VideoCategory()
                              .setEtag(createRandomString())
                              .setId(id)
                              .setKind(createRandomString())
                              .setSnippet(videoCategorySnippet);
                  })
                  .collect(Collectors.toList());
    }


    public static List<VideoCategory> createRandomListOfVideoCategories(int size) {
        return IntStream.range(0, size)
                        .mapToObj(i -> new VideoCategory(
                                UUID.randomUUID(),
                                createRandomString(),
                                createRandomString(),
                                Instant.now(),
                                Instant.now()))
                        .collect(Collectors.toList());
    }


    public static List<VideoCategory> createListOfVideoCategoriesGivenYTIds(List<String> youtubeIds) {
        return youtubeIds.stream()
                         .map(youtubeId -> new VideoCategory(
                                 UUID.randomUUID(),
                                 createRandomString(),
                                 youtubeId,
                                 Instant.now(),
                                 Instant.now()))
                         .collect(Collectors.toList());
    }


    public static UserVideo createUserVideo(com.jlisok.youtube_activity_manager.videos.models.Video video, User user, Rating rating) {
        UUID id = UUID.randomUUID();
        return new UserVideo(id, user, video, rating);
    }


    private static List<String> createRandomTags() {
        return IntStream.range(0, random.nextInt(10))
                        .mapToObj(i -> createRandomString())
                        .collect(Collectors.toList());
    }


    private static VideoCategory createRandomVideoCategory() {
        UUID id = UUID.randomUUID();
        String categoryName = createRandomString();
        String youtubeId = createRandomString();
        Instant now = Instant.now();
        return new VideoCategory(id, categoryName, youtubeId, now, now);
    }


    public static String createRandomString() {
        return RandomStringUtils.randomAlphanumeric(20);
    }
}
