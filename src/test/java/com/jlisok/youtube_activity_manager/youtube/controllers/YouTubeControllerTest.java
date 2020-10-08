package com.jlisok.youtube_activity_manager.youtube.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.jlisok.youtube_activity_manager.testutils.AuthenticationUtils;
import com.jlisok.youtube_activity_manager.testutils.MockMvcBasicRequestBuilder;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
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
import org.springframework.validation.BindException;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
    private UserUtils userUtils;

    @Autowired
    private GoogleIdTokenVerifier tokenVerifier;

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


    private final String accessToken = "ya29.a0AfH6SMDlGKLfUTTDHrjYyQq2tBUukALHRH3jczxHbuOcXoQmVm2WG8bkczV2m3NmXGPFlvgAyjNI3Vo_w1YQUCiAFq81C_cm7kggdg3P44Nip0oCfgGie4BA1Tw23YEGbXrfrDvQObP-LymoUgzInLjJJ3amNSd-1SM8";
    private final String googleIdTokenString = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjdkYTc4NjNlODYzN2Q2NjliYzJhMTI2MjJjZWRlMmE4ODEzZDExYjEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiODQ1MTYxMjIxMjUxLThxY2pqbnFtM2E1NjhwMG05YWxhanYya2FhNTE0am90LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiODQ1MTYxMjIxMjUxLThxY2pqbnFtM2E1NjhwMG05YWxhanYya2FhNTE0am90LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTE1MzEwMzM0NDEyNzQ5NTY2MDEzIiwiZW1haWwiOiJqdXN0eW5hLmxpc29rQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiaExJa0I3NDE5Zml3NjFSb25RVGY3QSIsIm5hbWUiOiJKdXN0eW5hIExpc29rIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tbDlMSEdteGptWmcvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQU1adXVjbDNqYjg4TGRZM2s4LUVRTUxFYldsZVZRZXIwZy9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiSnVzdHluYSIsImZhbWlseV9uYW1lIjoiTGlzb2siLCJsb2NhbGUiOiJwbCIsImlhdCI6MTYwMjE4MDI2NiwiZXhwIjoxNjAyMTgzODY2LCJqdGkiOiJhMDMyZjQwNDE4ZmQ0Y2VlMzI3NjBjYTliMTdlOTIxYjU2NDYwYTRmIn0.AoboNAgY1wUaGLSjfeFbfTrv56n_9fAi_nM5jga_IFJrIT1dh1NTfQ7cllFNbcPPkGlS7UwiRc161FlnmvWIYNEacC0kHa4FRuhzdxTn6w_JwhnDlMKHl2kr9bwMfBuFwKs6MCWhvtMba6y127s3iCHGcyZdq7OYWJ9fiZYJVBa4q98R58RgA2mcpnXGiN-FfTVYTOW5cAu_UmTsjrEWW3IrjEzrF9uVrxidMdDAdmq8qodXgPlpuiFrA2morAbwyY5MEYw9ILWYXtmQ3gEBmI1vPi_WyNw532KRy5lcL6oWC7WJbVTq7PqwuY8e56d7KfCVNuUrzXdLIr0QgihFMg";
    private final String invalidToken = "gfdes";
    private final String endPointUrlVideos = "/api/v1/youtube/videos";
    private final String endPointUrlChannels = "/api/v1/youtube/channels";
    private final String parameterName = "rating";
    private final String parameterValue = Rating.LIKE.toString();

    private String jsonHeader;
    private User user;


    @BeforeEach
    public void createYoutubeRequestDto() throws GeneralSecurityException, IOException {
        GoogleIdToken googleIdToken = tokenVerifier.verify(googleIdTokenString);
        user = userUtils.insertUserInDatabase(googleIdTokenString, googleIdToken, accessToken);
        jsonHeader = utils.createRequestAuthenticationHeader(user.getId().toString());
    }


    @Test
    void getRatedVideos_whenRequestValid_RatingLike() throws Exception {
        //given
        assertFalse(userVideoRepository.existsByUserId(user.getId()));
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setGetRequestWithParams(endPointUrlVideos, jsonHeader, parameterName, parameterValue))
                .andExpect(status().isOk())
                .andExpect(YouTubeEntityVerifier::assertResponseVideosNotNull);
    }


    @Test
    void getRatedVideos_whenRequestValid_RatingDislike() throws Exception {
        //given
        assertFalse(userVideoRepository.existsByUserId(user.getId()));
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setGetRequestWithParams(endPointUrlVideos, jsonHeader, parameterName, Rating.DISLIKE
                        .toString()))
                .andExpect(status().isOk())
                .andExpect(YouTubeEntityVerifier::assertResponseVideosNotNull);
    }


    @Test
    void getRatedVideos_whenNullRating() throws Exception {
        //given
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setGetRequestWithParams(endPointUrlVideos, jsonHeader, parameterName, null))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BindException));
    }


    @Test
    void getRatedVideos_whenTokenInvalid() throws Exception {
        //given

        when(tokenService.getAccessToken())
                .thenReturn(invalidToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setGetRequestWithParams(endPointUrlVideos, jsonHeader, parameterName, parameterValue))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }


    @Test
    void getSubscribedChannels() throws Exception {
        //given
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlChannels, jsonHeader, ""))
                .andExpect(status().isOk())
                .andExpect(YouTubeEntityVerifier::assertResponseChannelsNotNull);
    }


    @Test
    void getSubscribedChannels_whenTokenInvalid() throws Exception {
        //given
        when(tokenService.getAccessToken())
                .thenReturn(invalidToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlChannels, jsonHeader, ""))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }
}