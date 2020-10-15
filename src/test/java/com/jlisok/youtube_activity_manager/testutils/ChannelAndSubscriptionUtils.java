package com.jlisok.youtube_activity_manager.testutils;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Sets;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.models.ChannelBuilder;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChannelAndSubscriptionUtils {

    private final static Random random = new Random();


    public static List<com.google.api.services.youtube.model.Channel> createRandomYouTubeChannelList(int size) {
        if (size == 0) {
            return new ArrayList<>(0);
        }
        return IntStream.range(0, size)
                        .mapToObj(i -> {
                            String id = UUID.randomUUID().toString();
                            ChannelSnippet snippet = createRandomChannelSnippet();
                            ChannelStatistics statistics = createChannelStatistics();
                            ChannelTopicDetails category = createChannelTopicDetails();
                            ChannelContentOwnerDetails owner = createChannelContentOwnerDetails();
                            return new com.google.api.services.youtube.model.Channel()
                                    .setId(id)
                                    .setSnippet(snippet)
                                    .setStatistics(statistics)
                                    .setTopicDetails(category)
                                    .setContentOwnerDetails(owner);
                        })
                        .collect(Collectors.toList());
    }


    public static List<com.jlisok.youtube_activity_manager.channels.models.Channel> createRandomListOfChannels(int size, User... user) {
        return IntStream.range(0, size)
                        .mapToObj(i -> createRandomChannel(user))
                        .collect(Collectors.toList());
    }


    public static ChannelListResponse createChannelListResponse(List<com.google.api.services.youtube.model.Channel> channels) {
        return new ChannelListResponse()
                .setItems(channels);
    }


    public static Channel createRandomChannel(User... user) {
        String id = createRandomString();
        Set<User> users = Stream.of(user).collect(Collectors.toSet());
        return EntityCreator.createChannel(id, createRandomChannelSnippet(), createChannelStatistics(), createChannelContentOwnerDetails(), users);
    }


    public static List<Channel> copyOfMinus30MinutesCreatedAt(List<Channel> channels, User user) {
        return channels.stream()
                       .map(channel -> {
                           Channel repositoryChannel = ChannelAndSubscriptionUtils.copyOf(channel);
                           repositoryChannel.setId(UUID.randomUUID());
                           repositoryChannel.setCreatedAt(Instant.now().minus(Duration.ofMinutes(30)));
                           repositoryChannel.setModifiedAt(Instant.now().minus(Duration.ofMinutes(30)));
                           repositoryChannel.setUsers(Sets.newHashSet(user));
                           return repositoryChannel;
                       }).collect(Collectors.toList());
    }


    public static List<Channel> copyOfMinus30MinutesCreatedAt(List<Channel> channels, User user, List<com.google.api.services.youtube.model.Channel> youtubeChannels) {
        return IntStream.range(0, channels.size()).mapToObj(i -> {
            Channel repositoryChannel = ChannelAndSubscriptionUtils.copyOf(channels.get(i));
            repositoryChannel.setId(UUID.randomUUID());
            repositoryChannel.setYouTubeChannelId(youtubeChannels.get(i).getId());
            repositoryChannel.setCreatedAt(Instant.now().minus(Duration.ofMinutes(30)));
            repositoryChannel.setModifiedAt(Instant.now().minus(Duration.ofMinutes(30)));
            repositoryChannel.setUsers(Sets.newHashSet(user));
            return repositoryChannel;
        }).collect(Collectors.toList());
    }


    public static List<Subscription> createRandomSubscriptionList(int size) {
        return IntStream.range(0, size)
                        .mapToObj(i -> createSubscription())
                        .collect(Collectors.toList());
    }


    public static SubscriptionListResponse createSubscriptionListResponse(List<Subscription> subscriptions, String nextPageToken) {
        return new SubscriptionListResponse()
                .setNextPageToken(nextPageToken)
                .setItems(subscriptions);
    }


    private static Subscription createSubscription() {
        ResourceId resourceId = new ResourceId().setChannelId(createRandomString());
        SubscriptionSnippet snippet = new SubscriptionSnippet().setResourceId(resourceId);
        return new Subscription().setSnippet(snippet);
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
                .setViewCount(BigInteger.valueOf(random.nextInt()))
                .setSubscriberCount(BigInteger.valueOf(random.nextInt()))
                .setVideoCount(BigInteger.valueOf(random.nextInt()));
    }


    private static ChannelContentOwnerDetails createChannelContentOwnerDetails() {
        return new ChannelContentOwnerDetails()
                .setContentOwner(createRandomString());
    }


    private static ChannelTopicDetails createChannelTopicDetails() {
        return new ChannelTopicDetails()
                .setTopicCategories(Collections.singletonList(createRandomString()));
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


    public static String createRandomString() {
        return RandomStringUtils.randomAlphanumeric(20);
    }
}
