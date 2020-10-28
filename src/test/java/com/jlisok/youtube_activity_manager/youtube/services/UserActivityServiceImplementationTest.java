package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import com.jlisok.youtube_activity_manager.testutils.*;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.UserActivityDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.setAuthenticationInContext;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserActivityServiceImplementationTest implements TestProfile {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserActivityService userActivityService;

    @MockBean
    private UserVideoRepository userVideoRepository;

    @MockBean
    private ChannelRepository channelRepository;

    @MockBean
    private SynchronizationRepository synchronizationRepository;


    private final Rating rating = Rating.LIKE;
    private final String dummyToken = "dummytokendummytokendummytokendummytokendummytokendummytoken";

    private User user;
    private List<Channel> channels;
    private List<UserVideo> userVideos;
    private SynchronizationStatus status;

    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        user = userUtils.createUser(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        channels = ChannelAndSubscriptionUtils.createRandomListOfChannels(20, user);
        var videos = VideoUtils.createRandomListOfVideos(channels.size());
        userVideos = VideoUtils.createListOfUserVideos(videos, user, rating);
        setAuthenticationInContext(dummyToken, user.getId());
        status = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.SUCCEEDED, Instant.now(), user);

    }


    @Test
    void getRatedVideos_whenDatabaseEmptyAndStatusEmpty() {
        //given
        when(userVideoRepository.findByUserIdAndRating(user.getId(), rating))
                .thenReturn(Lists.emptyList());

        when(synchronizationRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(Optional.empty());

        //when
        UserActivityDto<VideoDto> videoDtos = userActivityService.getRatedVideos(rating);

        //then
        Assertions.assertNotNull(videoDtos);
        Assertions.assertNull(videoDtos.getState());
        Assertions.assertTrue(videoDtos.getYouTubeActivities().isEmpty());
    }


    @Test
    void getRatedVideos_whenLikeRatingAndStatusSucceeded() {
        //given
        when(userVideoRepository.findByUserIdAndRating(user.getId(), rating))
                .thenReturn(userVideos);

        when(synchronizationRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(Optional.of(status));

        //when
        UserActivityDto<VideoDto> videoDtos = userActivityService.getRatedVideos(rating);

        //then
        Assertions.assertNotNull(videoDtos);
        Assertions.assertNotNull(videoDtos.getState());
        Assertions.assertEquals(status.getState(), videoDtos.getState());
        videoDtos.getYouTubeActivities().forEach(YouTubeEntityVerifier::assertVideoDtoNotEmpty);
    }


    @Test
    void getRatedVideos_whenDislikeRating() {
        //given
        var status = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.IN_PROGRESS, Instant.now(), user);

        when(userVideoRepository.findByUserIdAndRating(user.getId(), Rating.DISLIKE))
                .thenReturn(userVideos);

        when(synchronizationRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(Optional.of(status));

        //when
        UserActivityDto<VideoDto> videoDtos = userActivityService.getRatedVideos(Rating.DISLIKE);

        //then
        Assertions.assertNotNull(videoDtos);
        Assertions.assertNotNull(videoDtos.getState());
        Assertions.assertEquals(status.getState(), videoDtos.getState());
        videoDtos.getYouTubeActivities().forEach(YouTubeEntityVerifier::assertVideoDtoNotEmpty);
    }


    @Test
    void getSubscribedChannels_whenDatabaseEmpty() {
        //given
        var statusLast = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.SUCCEEDED, Instant.now(), user);


        when(channelRepository.findByUsers_Id(user.getId()))
                .thenReturn(Lists.emptyList());

        when(synchronizationRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(Optional.of(statusLast));

        //when
        UserActivityDto<ChannelDto> channelDtos = userActivityService.getSubscribedChannels();

        //then
        Assertions.assertNotNull(channelDtos);
        Assertions.assertNotNull(channelDtos.getState());
        Assertions.assertEquals(statusLast.getState(), channelDtos.getState());
        Assertions.assertTrue(channelDtos.getYouTubeActivities().isEmpty());
    }


    @Test
    void getSubscribedChannels_whenChannelsInDatabase() {
        //given
        var status = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.FAILED, Instant.now(), user);

        when(channelRepository.findByUsers_Id(user.getId()))
                .thenReturn(channels);

        when(synchronizationRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(Optional.of(status));

        //when
        UserActivityDto<ChannelDto> channelDtos = userActivityService.getSubscribedChannels();

        //then
        Assertions.assertNotNull(channelDtos);
        Assertions.assertNotNull(channelDtos.getState());
        Assertions.assertEquals(status.getState(), channelDtos.getState());
        channelDtos.getYouTubeActivities().forEach(YouTubeEntityVerifier::assertChannelDtoNotEmpty);
    }

}