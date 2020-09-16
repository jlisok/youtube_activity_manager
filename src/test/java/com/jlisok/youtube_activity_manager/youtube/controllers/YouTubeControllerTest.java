package com.jlisok.youtube_activity_manager.youtube.controllers;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.jlisok.youtube_activity_manager.testutils.AuthenticationUtils;
import com.jlisok.youtube_activity_manager.testutils.MockMvcBasicRequestBuilder;
import com.jlisok.youtube_activity_manager.testutils.YouTubeListVerifier;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;
import com.jlisok.youtube_activity_manager.youtube.utils.AccessTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils.assertVideoNotEmpty;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Disabled("test methods require refreshed access token, which is now not implemented. In due time, this test class should be run manually.")
class YouTubeControllerTest {

    @Autowired
    private MockMvcBasicRequestBuilder mvcRequestBuilder;

    @Autowired
    private UserVideoRepository userVideoRepository;

    @Autowired
    private AuthenticationUtils utils;

    @MockBean
    private AccessTokenService tokenService;

    @Autowired
    private MockMvc mockMvc;

    private final String accessToken = "ya29.a0AfH6SMC38cOOPyXX9Lkt4L54qcmh3cgBcKYlDz3hRUuV15Yqo145PryyxQmIBH1bo9XEriNquwaB5Acnkn47-Bdgb36toxQFYhA8UYz6Ahv4T4TfAPm5zBuDScVGEdmyjlJehQM4p6j5ZLBGo-9H3idshTc2xjSh4ISP";
    private final String invalidToken = "gfdes";
    private final UUID userId = UUID.fromString("16b5d624-cc36-403c-835e-d1988c2410a8");
    private final String endPointUrl = "/api/v1/youtube/videos";

    private YouTubeRatingDto dto;
    private String jsonHeader;


    @BeforeEach
    public void createYoutubeRequestDto() {
        dto = new YouTubeRatingDto(Rating.LIKE);
        jsonHeader = utils.createRequestAuthenticationHeader(userId.toString());
    }


    @Test
    void getRatedVideos_whenRequestValid_RatingLike() throws Exception {
        //given
        assertTrue(userVideoRepository.findByUserId(userId).isEmpty());
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrl, jsonHeader, dto))
                .andExpect(status().isOk())
                .andExpect(YouTubeListVerifier::assertResponseListNotNull);


        List<UserVideo> userVideoList = userVideoRepository.findByUserId(userId);
        assertFalse(userVideoList.isEmpty());
        userVideoList.forEach(userVideo -> assertVideoNotEmpty(userVideo.getVideo()));
    }


    @Test
    void getRatedVideos_whenRequestValid_RatingDislike() throws Exception {
        //given
        assertTrue(userVideoRepository.findByUserId(userId).isEmpty());
        YouTubeRatingDto dto = new YouTubeRatingDto(Rating.DISLIKE);
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrl, jsonHeader, dto))
                .andExpect(status().isOk())
                .andExpect(YouTubeListVerifier::assertResponseListNotNull);

        List<UserVideo> userVideoList = userVideoRepository.findByUserId(userId);
        assertFalse(userVideoList.isEmpty());
        userVideoList.forEach(userVideo -> assertVideoNotEmpty(userVideo.getVideo()));
    }


    @Test
    void getRatedVideos_whenNullRating() throws Exception {
        //given
        YouTubeRatingDto badRatingDto = new YouTubeRatingDto(null);
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrl, jsonHeader, badRatingDto))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }


    @Test
    void getRatedVideos_whenTokenInvalid() throws Exception {
        //given

        when(tokenService.getAccessToken())
                .thenReturn(invalidToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrl, jsonHeader, dto))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }


    //TODO: in due time changing flow
    @Test
    void getSubscribedChannels() throws Exception {
        //given
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrl, jsonHeader, dto))
                .andExpect(status().isOk())
                .andExpect(YouTubeListVerifier::assertResponseListNotNull);
    }

    //TODO: in due time changing flow
    @Test
    void getSubscribedChannels_whenTokenInvalid() throws Exception {
        //given
        when(tokenService.getAccessToken())
                .thenReturn(invalidToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrl, jsonHeader, dto))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }
}