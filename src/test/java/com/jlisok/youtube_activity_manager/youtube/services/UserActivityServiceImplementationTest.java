package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channel.models.Channel;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserActivityServiceImplementationTest {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserActivityService userActivityService;

    @MockBean
    private YouTubeService youTubeService;

    @Captor
    ArgumentCaptor<YouTubeRatingDto> videoCaptor;


    private final Random random = new Random();
    private final YouTubeRatingDto dto = new YouTubeRatingDto(Rating.LIKE);

    private User user;
    private List<Channel> channels;
    private List<Video> videos;

    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        user = userUtils.createUser(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        channels = YouTubeApiUtils.createRandomListOfChannels(random.nextInt(40), user);
        videos = YouTubeApiUtils.createRandomListOfVideos(channels.size(), user);
    }

    @Test
    void getRatedVideos_whenRatingLike() throws IOException {
        //given
        when(youTubeService.listRatedVideos(dto)).thenReturn(videos);

        //when
        List<VideoDto> videoDtos = userActivityService.getRatedVideos(dto);

        //then
        verify(youTubeService).listRatedVideos(videoCaptor.capture());
        Assertions.assertNotNull(videoDtos);
        videoDtos.forEach(YouTubeEntityVerifier::assertVideoDtoNotEmpty);
    }


    @Test
    void getRatedVideos_whenRatingDisLike() throws IOException {
        //given
        YouTubeRatingDto dto = new YouTubeRatingDto(Rating.DISLIKE);

        when(youTubeService.listRatedVideos(dto)).thenReturn(videos);

        //when
        List<VideoDto> videoDtos = userActivityService.getRatedVideos(dto);

        //then
        verify(youTubeService).listRatedVideos(videoCaptor.capture());
        Assertions.assertNotNull(videoDtos);
        videoDtos.forEach(YouTubeEntityVerifier::assertVideoDtoNotEmpty);
    }


    @Test
    void getSubscribedChannels() throws IOException {
        //given
        when(youTubeService.listSubscribedChannels())
                .thenReturn(channels);

        //when
        List<ChannelDto> videoDtos = userActivityService.getSubscribedChannels();

        //then
        Assertions.assertNotNull(videoDtos);
        videoDtos.forEach(YouTubeEntityVerifier::assertChannelDtoNotEmpty);
    }
}