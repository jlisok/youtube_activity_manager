package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.youtube.api.YouTubeClient;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;
import com.jlisok.youtube_activity_manager.youtube.utils.AccessTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.setAuthenticationInContext;
import static com.jlisok.youtube_activity_manager.youtube.constants.YouTubeApiClientRequest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class YouTubeServiceImplementationTest {

    @Autowired
    private YouTubeService service;

    @Autowired
    private UserUtils userUtils;

    @MockBean
    private YouTubeClient client;

    @MockBean
    private AccessTokenService accessTokenService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserVideoRepository userVideoRepository;

    @MockBean
    private ChannelRepository channelRepository;

    @SuppressWarnings("unchecked")
    private static final Answer<List<UserVideo>> interceptUserVideos = invocation -> (List<UserVideo>) invocation.getArguments()[0];

    @SuppressWarnings("unchecked")
    private static final Answer<List<Channel>> interceptChannels = invocation -> (List<Channel>) invocation.getArguments()[0];

    private final String dummyAccessToken = "ya29.a0AfH6SMC7u_oxA3QU9cqXJ2m8e7a9JOAToMJbWf-FXFCVxGVeuxA3AdL9DxjF_FtSh3yZLOppEd8lhOlZmZfdtCjByKaU7TgttwRb8TwiKNZbkNaKctO37PKq-mXvsfOe_1FUcv1LrnPsSoNUp_R4FsMaNbgXLJlRvUiH";
    private final String dummyGoogleIdToken = "ya29.a0AfH6SMC7u_oxA3QU9cqXJ2m8e7a9JOAToMJbWf-FXFCVxGVeuxA3AdL9DxjF_FtSh3yZLOppEd8lhOlZmZfdtCjByKaU7TgttwRb8TwiKNZbkNaKctO37PKq-mXvsfOe_1FUcv1LrnPsSoNUp_R4FsMaNbgXLJlRvUiH";
    private final Random random = new Random();

    private YouTubeRatingDto dto;
    private User user;
    private List<com.google.api.services.youtube.model.Video> youtubeVideos;
    private List<Subscription> youtubeSubscriptions;
    private List<com.google.api.services.youtube.model.Channel> youtubeChannels;


    @BeforeEach
    void createDto() throws RegistrationException {
        dto = new YouTubeRatingDto(Rating.LIKE);
        String email = userUtils.createRandomEmail();
        String password = userUtils.createRandomPassword();
        user = userUtils.createUser(email, password);
        youtubeSubscriptions = YouTubeApiUtils.createRandomSubscriptionList(random.nextInt(30));
        youtubeChannels = YouTubeApiUtils.createRandomYouTubeChannelList(youtubeSubscriptions.size());
        youtubeVideos = YouTubeApiUtils.createRandomYouTubeVideoList(youtubeSubscriptions.size(), youtubeChannels);
        setAuthenticationInContext(dummyGoogleIdToken, user.getId());
    }


    @Test
    void listRatedVideos_andClientReturnsEmptyList() throws Exception {
        //given
        when(accessTokenService.getAccessToken())
                .thenReturn(dummyAccessToken);

        when(client.fetchRatedVideos(dummyAccessToken, VIDEO_REQUEST_PARTS, dto.getRating()))
                .thenReturn(Collections.emptyList());

        //when
        List<Video> actualVideos = service.listRatedVideos(dto);

        //then
        assertNotNull(actualVideos);
        assertTrue(actualVideos.isEmpty());
    }


    @Test
    void listRatedVideos_whenDtoRatingDislike() throws Exception {
        //given
        YouTubeRatingDto dislikeDto = new YouTubeRatingDto(Rating.DISLIKE);
        when(accessTokenService.getAccessToken())
                .thenReturn(dummyAccessToken);

        when(client.fetchRatedVideos(dummyAccessToken, VIDEO_REQUEST_PARTS, dislikeDto.getRating()))
                .thenReturn(youtubeVideos);

        when(client.fetchChannels(eq(dummyAccessToken), eq(CHANNEL_REQUEST_PARTS), ArgumentMatchers.anyList()))
                .thenReturn(youtubeChannels);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(channelRepository.findByUsers_Id(user.getId()))
                .thenReturn(Collections.emptyList());

        when(channelRepository.saveAll(anyList()))
                .thenAnswer(interceptChannels);

        when(userVideoRepository.saveAll(ArgumentMatchers.anyList()))
                .thenAnswer(interceptUserVideos);

        //when
        List<Video> videos = service.listRatedVideos(dislikeDto);

        //then
        verify(channelRepository).saveAll(anyList());
        verify(userVideoRepository).saveAll(ArgumentMatchers.anyList());
        assertEquals(youtubeVideos.size(), videos.size());
        videos.forEach(YouTubeEntityVerifier::assertVideoNotEmpty);
    }


    @Test
    void listRatedVideos_whenDtoRatingLike() throws Exception {
        //given
        when(accessTokenService.getAccessToken())
                .thenReturn(dummyAccessToken);

        when(client.fetchRatedVideos(dummyAccessToken, VIDEO_REQUEST_PARTS, dto.getRating()))
                .thenReturn(youtubeVideos);

        when(client.fetchChannels(eq(dummyAccessToken), eq(CHANNEL_REQUEST_PARTS), ArgumentMatchers.anyList()))
                .thenReturn(youtubeChannels);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(channelRepository.findByUsers_Id(user.getId()))
                .thenReturn(Collections.emptyList());

        when(channelRepository.saveAll(anyList()))
                .thenAnswer(interceptChannels);


        when(userVideoRepository.saveAll(ArgumentMatchers.anyList()))
                .thenAnswer(interceptUserVideos);

        //when
        List<Video> videos = service.listRatedVideos(dto);

        //then
        verify(channelRepository).saveAll(anyList());
        verify(userVideoRepository).saveAll(anyList());
        assertEquals(youtubeVideos.size(), videos.size());
        videos.forEach(YouTubeEntityVerifier::assertVideoNotEmpty);
    }


    @Test
    void listSubscribedChannels_andNoChannelsInDatabase() throws Exception {
        //given
        when(accessTokenService.getAccessToken())
                .thenReturn(dummyAccessToken);

        when(client.fetchSubscriptions(dummyAccessToken, SUBSCRIPTION_REQUEST_PARTS))
                .thenReturn(youtubeSubscriptions);

        when(client.fetchChannels(eq(dummyAccessToken), eq(CHANNEL_REQUEST_PARTS), ArgumentMatchers.anyList()))
                .thenReturn(youtubeChannels);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(channelRepository.findByUsers_Id(user.getId()))
                .thenReturn(Collections.emptyList());

        when(channelRepository.saveAll(anyList()))
                .thenAnswer(interceptChannels);

        //when
        List<Channel> actualChannels = service.listSubscribedChannels();

        //then
        verify(channelRepository).saveAll(anyList());
        assertNotNull(actualChannels);
        assertEquals(youtubeChannels.size(), actualChannels.size());
        actualChannels.forEach(YouTubeEntityVerifier::assertChannelNotEmpty);
    }


    @Test
    void listSubscribedChannels_andClientReturnsEmptyList() throws Exception {
        //given
        when(accessTokenService.getAccessToken())
                .thenReturn(dummyAccessToken);

        when(client.fetchSubscriptions(dummyAccessToken, SUBSCRIPTION_REQUEST_PARTS))
                .thenReturn(Collections.emptyList());

        //when
        List<Channel> actualChannels = service.listSubscribedChannels();

        //then
        assertNotNull(actualChannels);
        assertTrue(actualChannels.isEmpty());
    }


    @Test
    void listSubscribedChannels_andAllChannelsAlreadyInDatabase() throws Exception {
        //given
        User userInDatabase = userUtils.createUser(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        List<Channel> channels = YouTubeApiUtils.createRandomListOfChannels(youtubeChannels.size(), userInDatabase);
        List<Channel> repositoryChannels = YouTubeApiUtils.copyOfMinus30MinutesCreatedAt(channels, userInDatabase, youtubeChannels);

        when(accessTokenService.getAccessToken())
                .thenReturn(dummyAccessToken);

        when(client.fetchSubscriptions(dummyAccessToken, SUBSCRIPTION_REQUEST_PARTS))
                .thenReturn(youtubeSubscriptions);

        when(client.fetchChannels(eq(dummyAccessToken), eq(CHANNEL_REQUEST_PARTS), ArgumentMatchers.anyList()))
                .thenReturn(youtubeChannels);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(channelRepository.findByUsers_Id(user.getId()))
                .thenReturn(repositoryChannels);

        when(channelRepository.saveAll(anyList()))
                .thenAnswer(interceptChannels);

        //when
        List<Channel> actualChannels = service.listSubscribedChannels();

        //then
        verify(channelRepository).saveAll(anyList());
        assertNotNull(actualChannels);
        actualChannels.forEach(YouTubeEntityVerifier::assertChannelNotEmpty);

        assertEquals(youtubeChannels.size(), actualChannels.size());
        IntStream.range(0, actualChannels.size())
                 .forEach(i -> {
                     Channel dbChannel = repositoryChannels.get(i);
                     Channel actualChannel = actualChannels.get(i);
                     Assertions.assertEquals(dbChannel.getCreatedAt(), actualChannel.getCreatedAt());
                     Assertions.assertTrue(dbChannel.getModifiedAt().isBefore(actualChannel.getModifiedAt()));
                     Assertions.assertEquals(dbChannel.getId(), actualChannel.getId());
                 });
    }
}