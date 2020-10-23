package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.*;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

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


    private final Rating rating = Rating.LIKE;
    private final String dummyToken = "dummytokendummytokendummytokendummytokendummytokendummytoken";

    private User user;
    private List<Channel> channels;
    private List<UserVideo> userVideos;

    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        user = userUtils.createUserWithDataFromToken(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        channels = ChannelAndSubscriptionUtils.createRandomListOfChannels(20, user);
        var videos = VideoUtils.createRandomListOfVideos(channels.size());
        userVideos = VideoUtils.createListOfUserVideos(videos, user, rating);
        setAuthenticationInContext(dummyToken, user.getId());
    }


    @Test
    void getRatedVideos_whenDatabaseEmpty() {
        //given
        when(userVideoRepository.findByUserIdAndRating(user.getId(), rating))
                .thenReturn(Lists.emptyList());

        //when
        List<VideoDto> videoDtos = userActivityService.getRatedVideos(rating);

        //then
        Assertions.assertNotNull(videoDtos);
        Assertions.assertTrue(videoDtos.isEmpty());
    }


    @Test
    void getRatedVideos_whenLikeRating() {
        //given
        when(userVideoRepository.findByUserIdAndRating(user.getId(), rating))
                .thenReturn(userVideos);

        //when
        List<VideoDto> videoDtos = userActivityService.getRatedVideos(rating);

        //then
        Assertions.assertNotNull(videoDtos);
        videoDtos.forEach(YouTubeEntityVerifier::assertVideoDtoNotEmpty);
    }


    @Test
    void getRatedVideos_whenDislikeRating() {
        //given
        when(userVideoRepository.findByUserIdAndRating(user.getId(), Rating.DISLIKE))
                .thenReturn(userVideos);

        //when
        List<VideoDto> videoDtos = userActivityService.getRatedVideos(Rating.DISLIKE);

        //then
        Assertions.assertNotNull(videoDtos);
        videoDtos.forEach(YouTubeEntityVerifier::assertVideoDtoNotEmpty);
    }


    @Test
    void getSubscribedChannels_whenDatabaseEmpty() {
        //given
        when(channelRepository.findByUsers_Id(user.getId()))
                .thenReturn(Lists.emptyList());

        //when
        List<ChannelDto> channelDtos = userActivityService.getSubscribedChannels();

        //then
        Assertions.assertNotNull(channelDtos);
        Assertions.assertTrue(channelDtos.isEmpty());
    }


    @Test
    void getSubscribedChannels_whenChannelsInDatabase() {
        //given
        when(channelRepository.findByUsers_Id(user.getId()))
                .thenReturn(channels);

        //when
        List<ChannelDto> channelDtos = userActivityService.getSubscribedChannels();

        //then
        Assertions.assertNotNull(channelDtos);
        channelDtos.forEach(YouTubeEntityVerifier::assertChannelDtoNotEmpty);
    }

}