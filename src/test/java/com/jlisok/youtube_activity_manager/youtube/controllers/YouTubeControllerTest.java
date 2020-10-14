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

    private final String accessToken = "ya29.a0AfH6SMCkJ9mQ408KmhDAeYVzpJ4vnGm3d5C15pSsQGrHm9pW6RDIz0BdNn6hqXni1AV9ndaRJ5o5HJyYpyBrFMv2L3u0Wn28fTPQImYvCDe2biVEppO4ucavDRgbwk-bYsC9-AJCOv4KSJg3IwsjvTASqeH9-7u6Tpupng";
    private final String googleIdTokenString = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjdkYTc4NjNlODYzN2Q2NjliYzJhMTI2MjJjZWRlMmE4ODEzZDExYjEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiODQ1MTYxMjIxMjUxLThxY2pqbnFtM2E1NjhwMG05YWxhanYya2FhNTE0am90LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiODQ1MTYxMjIxMjUxLThxY2pqbnFtM2E1NjhwMG05YWxhanYya2FhNTE0am90LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTE1MzEwMzM0NDEyNzQ5NTY2MDEzIiwiZW1haWwiOiJqdXN0eW5hLmxpc29rQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiNUFBWHlrdjFUZjhubzFvM1RNVm1FdyIsIm5hbWUiOiJKdXN0eW5hIExpc29rIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tbDlMSEdteGptWmcvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQU1adXVjbDNqYjg4TGRZM2s4LUVRTUxFYldsZVZRZXIwZy9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiSnVzdHluYSIsImZhbWlseV9uYW1lIjoiTGlzb2siLCJsb2NhbGUiOiJwbCIsImlhdCI6MTYwMjUxNzM2OSwiZXhwIjoxNjAyNTIwOTY5LCJqdGkiOiJiMzNjMjk3NzJjMTBiMjA1NWVkOGJiZDAwMWY2NmRhODIyMTMzNjFlIn0.bPe31T_RrIsB60zcd3WEndWlt0pvExFnmQqZ6t4UXOP6VEuLTkh_sIEQfDJh6n9iuwDWXLTpG3-ZvPn9SIQ7pD4jOQHXPj-e8qxwLhulmSKs9Ja6g0y50HSVTXnfRPedX8cK0JaWFxqQqToXtbfhgWPTbDP9X-cErss_1mM0IMqnSYVvJ-mep7FA3L04Fg4pu6hZV8uG8LarSp906d-CnwcwIugPbMGAaw_WrYUZEOoxDEU-s-0BuwX7vgusUDI5DUtSmdyoaDFWIWUCr4PJaG4N0wfDhAQ1pq_FQfFr5SeXh6rQ1TeKQ9a1VwZho1KXqiK-hTPgSJBrhMcGY2Izvw";
    private final String invalidToken = "gfdes";
    private final String endPointUrlVideos = "/api/v1/youtube/videos";
    private final String endPointUrlChannels = "/api/v1/youtube/channels";
    private final String parameterName = "rating";

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
                .perform(mvcRequestBuilder.setGetRequestWithParams(endPointUrlVideos, jsonHeader, parameterName, Rating.LIKE
                        .toString()))
                .andExpect(status().isOk())
                .andExpect(YouTubeEntityVerifier::assertVideoDtoNotNull);
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
                .andExpect(YouTubeEntityVerifier::assertVideoDtoNotNull);
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
                .perform(mvcRequestBuilder.setGetRequestWithParams(endPointUrlVideos, jsonHeader, parameterName, Rating.LIKE
                        .toString()))
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
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlChannels, jsonHeader))
                .andExpect(status().isOk())
                .andExpect(YouTubeEntityVerifier::assertChannelDtoNotNull);
    }


    @Test
    void getSubscribedChannels_whenTokenInvalid() throws Exception {
        //given
        when(tokenService.getAccessToken())
                .thenReturn(invalidToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlChannels, jsonHeader))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }
}