package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.jlisok.youtube_activity_manager.testutils.ChannelAndSubscriptionUtils;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.VideoUtils;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.youtube.constants.YouTubeApiClientRequest;
import com.jlisok.youtube_activity_manager.youtube.utils.YouTubeActivityGetter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.StringUtils.split;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class YouTubeClientImplementationTest implements TestProfile {

    @Autowired
    private YouTubeClient client;

    @MockBean
    private YouTubeBuilder builder;

    @MockBean
    private YouTube youTube;

    @MockBean
    private YouTubeActivityGetter getter;

    private final String dummyAccessToken = "ya29.a0AfH6SMBjEYihc9CqwtV623c6iIYpuTvQmnqpj_gdMDFQcvyXmx3y-ilOeIynkbiDjFfjQZtHYOgPN8hvfgDox-Gz9HO2bWEKW8uLSwK_tOXMNK9RaidhWlDIpeMik1j1AsVmP6XFZXMFQc4hahL8DCvbzOlXqdrZnVmppw";
    private final String subscriptionRequestParts = YouTubeApiClientRequest.SUBSCRIPTION_REQUEST_PARTS;
    private final String channelRequestParts = YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS;
    private final Rating rating = Rating.LIKE;
    private final UUID userId = UUID.randomUUID();


    private List<Subscription> subscriptions;
    private List<Channel> channels;
    private List<Video> videos;


    @BeforeEach
    void prepareInitialConditions() {
        subscriptions = ChannelAndSubscriptionUtils.createRandomSubscriptionList(YouTubeApiClientRequest.MAX_ALLOWED_RESULTS_PER_PAGE);
        channels = ChannelAndSubscriptionUtils.createRandomYouTubeChannelList(subscriptions.size());
        videos = VideoUtils.createRandomYouTubeVideoList(YouTubeApiClientRequest.MAX_ALLOWED_RESULTS_PER_PAGE, channels);
    }


    @Test
    void fetchSubscriptions_whenNextTokenEmpty() {
        //given
        String nextPageToken = "";
        SubscriptionListResponse subscriptionListResponse = ChannelAndSubscriptionUtils.createSubscriptionListResponse(subscriptions, nextPageToken);

        when(builder.get(dummyAccessToken))
                .thenReturn(youTube);


        when(getter.getYouTubeSubscriptions(youTube, subscriptionRequestParts, nextPageToken, userId))
                .thenReturn(subscriptionListResponse);

        //when
        List<Subscription> actualSubscriptions = client.fetchSubscriptions(dummyAccessToken, subscriptionRequestParts, userId);

        // then
        Assertions.assertNotNull(actualSubscriptions);
        Assertions.assertFalse(actualSubscriptions.isEmpty());
        Assertions.assertEquals(subscriptions.size(), actualSubscriptions.size());
    }


    @Test
    void fetchSubscriptions_whenNextTokenNotEmpty() {
        //given
        String nextPageToken = "notEmptyPageToken";
        List<SubscriptionListResponse> subscriptionResponses = new ArrayList<>();
        subscriptionResponses.add(ChannelAndSubscriptionUtils.createSubscriptionListResponse(subscriptions, nextPageToken));
        subscriptionResponses.add(ChannelAndSubscriptionUtils.createSubscriptionListResponse(subscriptions, ""));

        when(builder.get(dummyAccessToken))
                .thenReturn(youTube);


        when(getter.getYouTubeSubscriptions(eq(youTube), eq(subscriptionRequestParts), any(String.class), eq(userId)))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(subscriptionResponses));

        //when
        List<Subscription> actualSubscriptions = client.fetchSubscriptions(dummyAccessToken, subscriptionRequestParts, userId);

        // then
        Assertions.assertNotNull(actualSubscriptions);
        Assertions.assertFalse(actualSubscriptions.isEmpty());
        Assertions.assertEquals(subscriptions.size() * 2, actualSubscriptions.size());
    }


    @Test
    void fetchSubscriptions_whenNextTokenNull() {
        //given
        String nextPageToken = null;
        SubscriptionListResponse subscriptionListResponse = ChannelAndSubscriptionUtils.createSubscriptionListResponse(subscriptions, nextPageToken);

        when(builder.get(dummyAccessToken))
                .thenReturn(youTube);


        when(getter.getYouTubeSubscriptions(eq(youTube), eq(subscriptionRequestParts), any(String.class), eq(userId)))
                .thenReturn(subscriptionListResponse);

        //when
        List<Subscription> actualSubscriptions = client.fetchSubscriptions(dummyAccessToken, subscriptionRequestParts, userId);

        // then
        Assertions.assertNotNull(actualSubscriptions);
        Assertions.assertFalse(actualSubscriptions.isEmpty());
        Assertions.assertEquals(subscriptions.size(), actualSubscriptions.size());
    }


    @Test
    void fetchChannels_whenChannelIdsBelowMaxRequestCapacity() {
        //given
        String dummyInputIds = IntStream
                .range(0, YouTubeApiClientRequest.MAX_ALLOWED_RESULTS_PER_PAGE)
                .mapToObj(i -> "wdqwdqw")
                .collect(Collectors.joining(","));
        List<String> dummyChannelIds = Arrays.asList(split(dummyInputIds, ","));
        ChannelListResponse listResponse = ChannelAndSubscriptionUtils.createChannelListResponse(channels);

        when(builder.get(dummyAccessToken))
                .thenReturn(youTube);


        when(getter.getYouTubeChannels(eq(youTube), eq(channelRequestParts), any(String.class), eq(userId)))
                .thenReturn(listResponse);

        //when
        List<Channel> actualChannels = client.fetchChannels(dummyAccessToken, channelRequestParts, dummyChannelIds, userId);

        // then
        Assertions.assertNotNull(actualChannels);
        Assertions.assertFalse(actualChannels.isEmpty());
        Assertions.assertEquals(dummyChannelIds.size(), actualChannels.size());
    }


    @Test
    void fetchChannels_whenChannelIdsAboveMaxRequestCapacity() {
        //given
        List<String> dummyChannelIds = IntStream
                .range(0, YouTubeApiClientRequest.MAX_ALLOWED_RESULTS_PER_PAGE * 2)
                .mapToObj(i -> "wdqwdqw")
                .collect(Collectors.toList());

        ChannelListResponse listResponse = ChannelAndSubscriptionUtils.createChannelListResponse(channels);

        when(builder.get(dummyAccessToken))
                .thenReturn(youTube);


        when(getter.getYouTubeChannels(eq(youTube), eq(channelRequestParts), any(String.class), eq(userId)))
                .thenReturn(listResponse);

        //when
        List<Channel> actualChannels = client.fetchChannels(dummyAccessToken, channelRequestParts, dummyChannelIds, userId);

        // then
        Assertions.assertNotNull(actualChannels);
        Assertions.assertFalse(actualChannels.isEmpty());
        Assertions.assertEquals(dummyChannelIds.size(), actualChannels.size());
    }


    @Test
    void fetchVideos_whenNextTokenEmpty() {
        //given
        String nextPageToken = "";
        VideoListResponse listResponse = VideoUtils.createVideoListResponse(videos, nextPageToken);

        when(builder.get(dummyAccessToken))
                .thenReturn(youTube);


        when(getter.getYouTubeVideos(youTube, subscriptionRequestParts, rating, nextPageToken, userId))
                .thenReturn(listResponse);

        //when
        List<Video> actualVideos = client.fetchRatedVideos(dummyAccessToken, subscriptionRequestParts, rating, userId);

        // then
        Assertions.assertNotNull(actualVideos);
        Assertions.assertFalse(actualVideos.isEmpty());
        Assertions.assertEquals(videos.size(), actualVideos.size());
    }


    @Test
    void fetchVideos_whenNextTokenNotEmpty() {
        //given
        String nextPageToken = "notEmptyPageToken";
        List<VideoListResponse> listResponses = new ArrayList<>();
        listResponses.add(VideoUtils.createVideoListResponse(videos, nextPageToken));
        listResponses.add(VideoUtils.createVideoListResponse(videos, null));

        when(builder.get(dummyAccessToken))
                .thenReturn(youTube);


        when(getter.getYouTubeVideos(eq(youTube), eq(subscriptionRequestParts), eq(rating), any(String.class), eq(userId)))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(listResponses));

        //when
        List<Video> actualVideos = client.fetchRatedVideos(dummyAccessToken, subscriptionRequestParts, rating, userId);

        // then
        Assertions.assertNotNull(actualVideos);
        Assertions.assertFalse(actualVideos.isEmpty());
        Assertions.assertEquals(videos.size() * 2, actualVideos.size());
    }


    @Test
    void fetchVideos_whenNextTokenNull() {
        //given
        String nextPageToken = null;
        VideoListResponse videoListResponse = VideoUtils.createVideoListResponse(videos, nextPageToken);

        when(builder.get(dummyAccessToken))
                .thenReturn(youTube);


        when(getter.getYouTubeVideos(eq(youTube), eq(subscriptionRequestParts), eq(rating), any(String.class), eq(userId)))
                .thenReturn(videoListResponse);

        //when
        List<Video> actualVideos = client.fetchRatedVideos(dummyAccessToken, subscriptionRequestParts, rating, userId);

        // then
        Assertions.assertNotNull(actualVideos);
        Assertions.assertFalse(actualVideos.isEmpty());
        Assertions.assertEquals(videos.size(), actualVideos.size());
    }
}
