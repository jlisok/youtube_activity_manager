package com.jlisok.youtube_activity_manager.testutils;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Sets;
import com.jlisok.youtube_activity_manager.channel.models.Channel;
import com.jlisok.youtube_activity_manager.channel.models.ChannelBuilder;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import com.jlisok.youtube_activity_manager.youtube.utils.VideoDescription;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigInteger;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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


    public static List<com.google.api.services.youtube.model.Channel> createRandomYouTubeChannelList(int loopEnd) {
        if (loopEnd == 0) {
            return new ArrayList<>(0);
        }
        List<com.google.api.services.youtube.model.Channel> channelList = new ArrayList<>(loopEnd);
        for (int i = 0; i < loopEnd; i++) {
            String id = UUID.randomUUID().toString();
            ChannelSnippet snippet = createRandomChannelSnippet();
            ChannelStatistics statistics = createChannelStatistics();
            ChannelTopicDetails category = createChannelTopicDetails();
            ChannelContentOwnerDetails owner = createChannelContentOwnerDetails();
            com.google.api.services.youtube.model.Channel channel = new com.google.api.services.youtube.model.Channel()
                    .setId(id)
                    .setSnippet(snippet)
                    .setStatistics(statistics)
                    .setTopicDetails(category)
                    .setContentOwnerDetails(owner);
            channelList.add(channel);
        }
        return channelList;
    }


    public static String createDescriptionWithRandomUriNumber() {
        return IntStream.range(0, random.nextInt(10))
                        .mapToObj(i -> uri.toString())
                        .collect(Collectors.joining(" "));
    }


    public static List<Video> createRandomListOfVideos(int size) {
        return IntStream.range(0, size)
                        .mapToObj(i -> createRandomVideo())
                        .collect(Collectors.toList());
    }


    public static Video createRandomVideo() {
        String videoId = createRandomString();
        List<String> uriList = VideoDescription.toListOfUri(createDescriptionWithRandomUriNumber());
        return EntityCreator.createVideo(videoId, createRandomVideoSnippet(), createRandomVideoContentDetails(), uriList);
    }


    public static List<com.jlisok.youtube_activity_manager.channel.models.Channel> createRandomListOfChannels(int size, User... user) {
        return IntStream.range(0, size)
                        .mapToObj(i -> createRandomChannel(user))
                        .collect(Collectors.toList());
    }


    public static Channel createRandomChannel(User... user) {
        String id = createRandomString();
        Set<User> users = Stream.of(user).collect(Collectors.toSet());
        return EntityCreator.createChannel(id, createRandomChannelSnippet(), createChannelStatistics(), createChannelContentOwnerDetails(), users);
    }

    public static List<Channel> copyOfMinus30MinutesCreatedAt(List<Channel> channels, User user) {
        return channels.stream()
                       .map(channel -> {
                           Channel repositoryChannel = YouTubeApiUtils.copyOf(channel);
                           repositoryChannel.setId(UUID.randomUUID());
                           repositoryChannel.setCreatedAt(Instant.now().minus(Duration.ofMinutes(30)));
                           repositoryChannel.setModifiedAt(Instant.now().minus(Duration.ofMinutes(30)));
                           repositoryChannel.setUsers(Sets.newHashSet(user));
                           return repositoryChannel;
                       }).collect(Collectors.toList());
    }

    public static List<Channel> copyOfMinus30MinutesCreatedAt(List<Channel> channels, User user, List<com.google.api.services.youtube.model.Channel> youtubeChannels) {
        return IntStream.range(0, channels.size()).mapToObj(i -> {
            Channel repositoryChannel = YouTubeApiUtils.copyOf(channels.get(i));
            repositoryChannel.setId(UUID.randomUUID());
            repositoryChannel.setYouTubeChannelId(youtubeChannels.get(i).getId());
            repositoryChannel.setCreatedAt(Instant.now().minus(Duration.ofMinutes(30)));
            repositoryChannel.setModifiedAt(Instant.now().minus(Duration.ofMinutes(30)));
            repositoryChannel.setUsers(Sets.newHashSet(user));
            return repositoryChannel;
        }).collect(Collectors.toList());
    }


    private static Channel copyOf(Channel channel) {
        return new ChannelBuilder()
                .setId(channel.getId())
                .setYouTubeChannelId(channel.getYouTubeChannelId())
                .setTitle(channel.getTitle())
                .setPublishedAt(channel.getPublishedAt())
                .setLanguage(channel.getLanguage())
                .setCountry(channel.getCountry())
                .setOwner(channel.getOwner())
                .setSubscriberNumber(channel.getSubscriberNumber())
                .setVideoNumber(channel.getVideoNumber())
                .setViewNumber(channel.getViewNumber())
                .setCreatedAt(channel.getCreatedAt())
                .setModifiedAt(channel.getModifiedAt())
                .createChannel();
    }


    public static List<Subscription> createRandomSubscriptionList(int size) {
        return IntStream.range(0, size)
                        .mapToObj(i -> createSubscription())
                        .collect(Collectors.toList());
    }


    private static Subscription createSubscription() {
        ResourceId resourceId = new ResourceId().setChannelId(createRandomString());
        SubscriptionSnippet snippet = new SubscriptionSnippet().setResourceId(resourceId);
        return new Subscription().setSnippet(snippet);
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


    private static ChannelSnippet createRandomChannelSnippet() {
        return new ChannelSnippet()
                .setTitle(createRandomString())
                .setDescription(createRandomString())
                .setDefaultLanguage(createRandomString())
                .setCountry("USA")
                .setPublishedAt(new DateTime(Instant.now().toEpochMilli()));
    }


    private static ChannelStatistics createChannelStatistics() {
        return new ChannelStatistics()
                .setViewCount(BigInteger.valueOf(new Random().nextInt()))
                .setSubscriberCount(BigInteger.valueOf(new Random().nextInt()))
                .setVideoCount(BigInteger.valueOf(new Random().nextInt()));
    }


    private static ChannelContentOwnerDetails createChannelContentOwnerDetails() {
        return new ChannelContentOwnerDetails()
                .setContentOwner(createRandomString());
    }


    private static ChannelTopicDetails createChannelTopicDetails() {
        return new ChannelTopicDetails()
                .setTopicCategories(Collections.singletonList(createRandomString()));
    }
}
