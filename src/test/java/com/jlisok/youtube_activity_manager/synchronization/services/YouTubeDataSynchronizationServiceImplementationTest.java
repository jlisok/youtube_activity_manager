package com.jlisok.youtube_activity_manager.synchronization.services;

import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import com.jlisok.youtube_activity_manager.testutils.*;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.youtube.api.YouTubeClient;
import com.jlisok.youtube_activity_manager.youtube.constants.YouTubeApiClientRequest;
import com.jlisok.youtube_activity_manager.youtube.exceptions.YouTubeApiException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class YouTubeDataSynchronizationServiceImplementationTest implements TestProfile {

    @Autowired
    private YouTubeDataSynchronizationServiceImplementation youTubeDataSynchronizationService;

    @Autowired
    private SynchronizationRepository synchronizationRepository;

    @Autowired
    private UserVideoRepository userVideoRepository;

    @Autowired
    private TestUserVideoRepository testUserVideoRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestChannelRepository testChannelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTestUtils userTestUtils;

    @MockBean
    private YouTubeClient client;

    private final String accessToken = "dummyAccessTokenDummyAccessToken";

    private User user;

    //rated Videos
    private List<com.google.api.services.youtube.model.Video> youtubeVideos;
    private List<com.google.api.services.youtube.model.Channel> youtubeChannelsRatedVideos;
    private List<String> youtubeVideoCategoryIds;
    private List<com.google.api.services.youtube.model.VideoCategory> youtubeVideoCategories;

    //subscribed Channels
    private List<Subscription> youtubeSubscriptions;
    private List<String> youtubeSubscribedChannelIds;
    private List<String> youtubeChannelRatedVideosIds;
    private List<com.google.api.services.youtube.model.Channel> youtubeSubscribedChannels;

    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        user = userTestUtils.insertUserInDatabase(userTestUtils.createRandomEmail(), userTestUtils.createRandomPassword());

        //rated Videos
        youtubeChannelsRatedVideos = ChannelAndSubscriptionUtils.createRandomYouTubeChannelList(1);
        youtubeChannelRatedVideosIds = youtubeChannelsRatedVideos.stream()
                                                                 .map(com.google.api.services.youtube.model.Channel::getId)
                                                                 .collect(Collectors.toList());
        youtubeVideos = VideoUtils.createRandomYouTubeVideoList(youtubeChannelsRatedVideos.size(), youtubeChannelsRatedVideos);
        youtubeVideoCategoryIds = youtubeVideos
                .stream()
                .map(video -> video.getSnippet().getCategoryId())
                .collect(Collectors.toList());
        youtubeVideoCategories = VideoUtils.createRandomListOfYouTubeVideoCategoriesById(youtubeVideoCategoryIds);

        //subscribed Channels
        youtubeSubscriptions = ChannelAndSubscriptionUtils.createRandomSubscriptionList(1);
        youtubeSubscribedChannelIds = youtubeSubscriptions
                .stream()
                .map(subscription -> subscription.getSnippet().getResourceId().getChannelId())
                .collect(Collectors.toList());
        youtubeSubscribedChannels = ChannelAndSubscriptionUtils.createRandomYouTubeChannelList(youtubeSubscriptions.size());
    }


    /**
     * Async methods do not work with @transactional. Must remove all added entities from database manually
     */
    @AfterEach
    void removeDataFromDatabase() {
        synchronizationRepository.deleteByUserId(user.getId());
        testUserVideoRepository.deleteAllByUserId(user.getId());
        testChannelRepository.deleteAllByUsers_Id(user.getId());
        userRepository.delete(user);
    }


    @Test
    void synchronize_whenAccessTokenExpired() {
        //given
        String expiredAccessToken = "expiredDummyAccessTokenExpiredDummyAccessToken";
        UUID synchronizationId = UUID.randomUUID();

        when(client.fetchSubscriptions(expiredAccessToken, YouTubeApiClientRequest.SUBSCRIPTION_REQUEST_PARTS, user.getId()))
                .thenThrow(new YouTubeApiException("Dummy message", new IOException()));

        //when
        youTubeDataSynchronizationService.synchronizeYouTubeData(synchronizationId, expiredAccessToken, user
                .getId()).join();

        //then
        SynchronizationState actualState = synchronizationRepository
                .findByUserId(user.getId())
                .map(SynchronizationStatus::getState)
                .orElse(null);

        Assertions.assertNotNull(actualState);
        Assertions.assertEquals(SynchronizationState.FAILED, actualState);
    }


    @Test
    void synchronize_whenSubscribedChannelsFetchedFromYouTubeClient() {
        //given
        UUID synchronizationId = UUID.randomUUID();

        //getSubscribedChannels
        when(client.fetchSubscriptions(accessToken, YouTubeApiClientRequest.SUBSCRIPTION_REQUEST_PARTS, user.getId()))
                .thenReturn(youtubeSubscriptions);

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, youtubeSubscribedChannelIds, user
                .getId()))
                .thenReturn(youtubeSubscribedChannels);

        //getRatedVideos
        when(client.fetchRatedVideos(eq(accessToken), eq(YouTubeApiClientRequest.VIDEO_REQUEST_PARTS), any(Rating.class), eq(user.getId())))
                .thenReturn(Lists.emptyList());

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), user.getId()))
                .thenReturn(Lists.emptyList());

        when(client.fetchVideoCategories(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), user
                .getId()))
                .thenReturn(Lists.emptyList());

        //when
        youTubeDataSynchronizationService.synchronizeYouTubeData(synchronizationId, accessToken, user
                .getId()).join();

        //then
        SynchronizationState actualState = synchronizationRepository
                .findByUserId(user.getId())
                .map(SynchronizationStatus::getState)
                .orElse(null);

        List<Channel> dbChannels = channelRepository.findByUsers_Id(user.getId());
        List<UserVideo> usersVideos = userVideoRepository.findByUserId(user.getId());

        Assertions.assertNotNull(actualState);
        Assertions.assertEquals(SynchronizationState.SUCCEEDED, actualState);

        Assertions.assertFalse(dbChannels.isEmpty());
        dbChannels.forEach(YouTubeEntityVerifier::assertChannelNotEmpty);
        YouTubeEntityVerifier.assertListOfChannelsEqual(youtubeSubscribedChannels, dbChannels);

        Assertions.assertTrue(usersVideos.isEmpty());
    }


    @Test
    void synchronize_whenUpdatingSameSubscribedChannels() {
        //given
        UUID synchronizationId = UUID.randomUUID();

        //getSubscribedChannels
        when(client.fetchSubscriptions(accessToken, YouTubeApiClientRequest.SUBSCRIPTION_REQUEST_PARTS, user.getId()))
                .thenReturn(youtubeSubscriptions);

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, youtubeSubscribedChannelIds, user
                .getId()))
                .thenReturn(youtubeSubscribedChannels);

        //getRatedVideos
        when(client.fetchRatedVideos(eq(accessToken), eq(YouTubeApiClientRequest.VIDEO_REQUEST_PARTS), any(Rating.class), eq(user.getId())))
                .thenReturn(Lists.emptyList());

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), user.getId()))
                .thenReturn(Lists.emptyList());

        when(client.fetchVideoCategories(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), user
                .getId()))
                .thenReturn(Lists.emptyList());

        //when
        youTubeDataSynchronizationService.synchronizeYouTubeData(synchronizationId, accessToken, user.getId()).join();
        youTubeDataSynchronizationService.synchronizeYouTubeData(synchronizationId, accessToken, user.getId()).join();

        //then
        SynchronizationState actualState = synchronizationRepository
                .findByUserId(user.getId())
                .map(SynchronizationStatus::getState)
                .orElse(null);

        List<Channel> dbChannels = channelRepository.findByUsers_Id(user.getId());
        List<UserVideo> usersVideos = userVideoRepository.findByUserId(user.getId());

        Assertions.assertNotNull(actualState);
        Assertions.assertEquals(SynchronizationState.SUCCEEDED, actualState);

        Assertions.assertFalse(dbChannels.isEmpty());
        dbChannels.forEach(YouTubeEntityVerifier::assertChannelNotEmpty);
        YouTubeEntityVerifier.assertListOfChannelsEqual(youtubeSubscribedChannels, dbChannels);

        Assertions.assertTrue(usersVideos.isEmpty());
    }


    @Test
    void synchronize_whenLikedVideosFetchedFromYouTubeClient() {
        //given
        UUID synchronizationId = UUID.randomUUID();

        //getSubscribedChannels
        when(client.fetchSubscriptions(accessToken, YouTubeApiClientRequest.SUBSCRIPTION_REQUEST_PARTS, user.getId()))
                .thenReturn(Lists.emptyList());

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), user.getId()))
                .thenReturn(Lists.emptyList());

        //getRatedVideos
        when(client.fetchRatedVideos(accessToken, YouTubeApiClientRequest.VIDEO_REQUEST_PARTS, Rating.LIKE, user.getId()))
                .thenReturn(youtubeVideos);

        when(client.fetchRatedVideos(accessToken, YouTubeApiClientRequest.VIDEO_REQUEST_PARTS, Rating.DISLIKE, user.getId()))
                .thenReturn(Lists.emptyList());

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, youtubeChannelRatedVideosIds, null))
                .thenReturn(youtubeChannelsRatedVideos);

        when(client.fetchVideoCategories(accessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, youtubeVideoCategoryIds, user
                .getId()))
                .thenReturn(youtubeVideoCategories);

        when(client.fetchVideoCategories(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), user
                .getId()))
                .thenReturn(Lists.emptyList());

        //when
        youTubeDataSynchronizationService.synchronizeYouTubeData(synchronizationId, accessToken, user
                .getId()).join();

        //then
        SynchronizationState actualState = synchronizationRepository
                .findByUserId(user.getId())
                .map(SynchronizationStatus::getState)
                .orElse(null);

        List<UserVideo> usersVideos = userVideoRepository.findByUserId(user.getId());
        List<Video> dbVideos = usersVideos
                .stream()
                .map(UserVideo::getVideo)
                .collect(Collectors.toList());
        List<Channel> dbChannels = dbVideos
                .stream()
                .map(Video::getChannel)
                .collect(Collectors.toList());
        List<VideoCategory> dbVideoCategories = dbVideos
                .stream()
                .map(Video::getVideoCategory)
                .collect(Collectors.toList());

        Assertions.assertNotNull(actualState);
        Assertions.assertEquals(SynchronizationState.SUCCEEDED, actualState);

        Assertions.assertFalse(usersVideos.isEmpty());
        usersVideos.forEach(uv -> Assertions.assertEquals(Rating.LIKE, uv.getRating()));

        Assertions.assertFalse(dbVideos.isEmpty());
        dbVideos.forEach(YouTubeEntityVerifier::assertVideoNotEmpty);
        YouTubeEntityVerifier.assertListOfVideosEqual(youtubeVideos, dbVideos);


        Assertions.assertFalse(dbVideoCategories.isEmpty());
        dbVideoCategories.forEach(YouTubeEntityVerifier::assertVideoCategoryNotEmpty);
        YouTubeEntityVerifier.assertYouTubeVideoCategoryAndVideoCategoryEqual(youtubeVideoCategories, dbVideoCategories);

        Assertions.assertFalse(dbChannels.isEmpty());
        dbChannels.forEach(channel -> {
            Assertions.assertTrue(channel.getUsers().isEmpty());
            YouTubeEntityVerifier.assertChannelNotEmpty(channel);
        });
        YouTubeEntityVerifier.assertListOfChannelsEqual(youtubeChannelsRatedVideos, dbChannels);
    }


    @Test
    void synchronize_whenDislikedVideosFetchedFromYouTubeClient() {
        //given
        UUID synchronizationId = UUID.randomUUID();

        //getSubscribedChannels
        when(client.fetchSubscriptions(accessToken, YouTubeApiClientRequest.SUBSCRIPTION_REQUEST_PARTS, user.getId()))
                .thenReturn(Lists.emptyList());

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), user.getId()))
                .thenReturn(Lists.emptyList());

        //getRatedVideos
        when(client.fetchRatedVideos(accessToken, YouTubeApiClientRequest.VIDEO_REQUEST_PARTS, Rating.DISLIKE, user.getId()))
                .thenReturn(youtubeVideos);

        when(client.fetchRatedVideos(accessToken, YouTubeApiClientRequest.VIDEO_REQUEST_PARTS, Rating.LIKE, user.getId()))
                .thenReturn(Lists.emptyList());

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, youtubeChannelRatedVideosIds, null))
                .thenReturn(youtubeChannelsRatedVideos);

        when(client.fetchVideoCategories(accessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, youtubeVideoCategoryIds, user
                .getId()))
                .thenReturn(youtubeVideoCategories);

        when(client.fetchVideoCategories(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), user
                .getId()))
                .thenReturn(Lists.emptyList());

        //when
        youTubeDataSynchronizationService.synchronizeYouTubeData(synchronizationId, accessToken, user
                .getId()).join();

        //then
        SynchronizationState actualState = synchronizationRepository
                .findByUserId(user.getId())
                .map(SynchronizationStatus::getState)
                .orElse(null);

        List<UserVideo> usersVideos = userVideoRepository.findByUserId(user.getId());
        List<Video> dbVideos = usersVideos
                .stream()
                .map(UserVideo::getVideo)
                .collect(Collectors.toList());
        List<Channel> dbChannels = dbVideos
                .stream()
                .map(Video::getChannel)
                .collect(Collectors.toList());
        List<VideoCategory> dbVideoCategories = dbVideos
                .stream()
                .map(Video::getVideoCategory)
                .collect(Collectors.toList());

        Assertions.assertNotNull(actualState);
        Assertions.assertEquals(SynchronizationState.SUCCEEDED, actualState);

        Assertions.assertFalse(usersVideos.isEmpty());
        usersVideos.forEach(uv -> Assertions.assertEquals(Rating.DISLIKE, uv.getRating()));

        Assertions.assertFalse(dbVideos.isEmpty());
        dbVideos.forEach(YouTubeEntityVerifier::assertVideoNotEmpty);
        YouTubeEntityVerifier.assertListOfVideosEqual(youtubeVideos, dbVideos);


        Assertions.assertFalse(dbVideoCategories.isEmpty());
        dbVideoCategories.forEach(YouTubeEntityVerifier::assertVideoCategoryNotEmpty);
        YouTubeEntityVerifier.assertYouTubeVideoCategoryAndVideoCategoryEqual(youtubeVideoCategories, dbVideoCategories);

        Assertions.assertFalse(dbChannels.isEmpty());
        dbChannels.forEach(channel -> {
            Assertions.assertTrue(channel.getUsers().isEmpty());
            YouTubeEntityVerifier.assertChannelNotEmpty(channel);
        });
        YouTubeEntityVerifier.assertListOfChannelsEqual(youtubeChannelsRatedVideos, dbChannels);
    }

    @Test
    void synchronize_whenUpdatingChannels() {
        //given
        UUID synchronizationId = UUID.randomUUID();

        /*getSubscribedChannels
            inserting youtubeChannelsRatedVideos into getSubscribedChannels, to see if they update in getRatedVideos
         */
        when(client.fetchSubscriptions(accessToken, YouTubeApiClientRequest.SUBSCRIPTION_REQUEST_PARTS, user.getId()))
                .thenReturn(youtubeSubscriptions);

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, youtubeSubscribedChannelIds, user
                .getId()))
                .thenReturn(youtubeChannelsRatedVideos);


        /* getRatedVideos
            incorporating the same list of channels to see whether they update properly
         */
        when(client.fetchRatedVideos(accessToken, YouTubeApiClientRequest.VIDEO_REQUEST_PARTS, Rating.LIKE, user.getId()))
                .thenReturn(youtubeVideos);

        when(client.fetchRatedVideos(accessToken, YouTubeApiClientRequest.VIDEO_REQUEST_PARTS, Rating.DISLIKE, user.getId()))
                .thenReturn(Lists.emptyList());

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, youtubeChannelRatedVideosIds, null))
                .thenReturn(youtubeChannelsRatedVideos);

        when(client.fetchChannels(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), null))
                .thenReturn(Lists.emptyList());

        when(client.fetchVideoCategories(accessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, youtubeVideoCategoryIds, user
                .getId()))
                .thenReturn(youtubeVideoCategories);

        when(client.fetchVideoCategories(accessToken, YouTubeApiClientRequest.CHANNEL_REQUEST_PARTS, Lists.emptyList(), user
                .getId()))
                .thenReturn(Lists.emptyList());

        //when
        youTubeDataSynchronizationService.synchronizeYouTubeData(synchronizationId, accessToken, user
                .getId()).join();

        //then
        SynchronizationState actualState = synchronizationRepository
                .findByUserId(user.getId())
                .map(SynchronizationStatus::getState)
                .orElse(null);

        List<UserVideo> usersVideos = userVideoRepository.findByUserId(user.getId());
        List<Video> dbVideos = usersVideos
                .stream()
                .map(UserVideo::getVideo)
                .collect(Collectors.toList());
        List<Channel> dbChannels = dbVideos
                .stream()
                .map(Video::getChannel)
                .collect(Collectors.toList());
        List<VideoCategory> dbVideoCategories = dbVideos
                .stream()
                .map(Video::getVideoCategory)
                .collect(Collectors.toList());

        Assertions.assertNotNull(actualState);
        Assertions.assertEquals(SynchronizationState.SUCCEEDED, actualState);

        Assertions.assertFalse(usersVideos.isEmpty());
        usersVideos.forEach(uv -> Assertions.assertEquals(Rating.LIKE, uv.getRating()));

        Assertions.assertFalse(dbVideos.isEmpty());
        dbVideos.forEach(YouTubeEntityVerifier::assertVideoNotEmpty);
        YouTubeEntityVerifier.assertListOfVideosEqual(youtubeVideos, dbVideos);


        Assertions.assertFalse(dbVideoCategories.isEmpty());
        dbVideoCategories.forEach(YouTubeEntityVerifier::assertVideoCategoryNotEmpty);
        YouTubeEntityVerifier.assertYouTubeVideoCategoryAndVideoCategoryEqual(youtubeVideoCategories, dbVideoCategories);

        Assertions.assertFalse(dbChannels.isEmpty());
        dbChannels.forEach(channel -> {
            Assertions.assertFalse(channel.getUsers().isEmpty());
            YouTubeEntityVerifier.assertChannelNotEmpty(channel);
        });
        YouTubeEntityVerifier.assertListOfChannelsEqual(youtubeChannelsRatedVideos, dbChannels);
    }
}