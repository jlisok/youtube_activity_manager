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

    private final String accessToken = "ya29.a0AfH6SMD3PLkPZPJ3YQoC6wAJdljv03l3DtGuddxwlmkzTP3qgwU1H3In8Sz5wN_OT_vcKUhspa2VZ6r5nErKM7e3CdIg709LnLWGQIVtCOFX4xd53mtF8cCr0fay1kK6knW6ff8OEqus1rXr990z0Lf_X5wMThbQXF-A";
    private final String googleIdTokenString = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjVlZmZhNzZlZjMzZWNiNWUzNDZiZDUxMmQ3ZDg5YjMwZTQ3ZDhlOTgiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiODQ1MTYxMjIxMjUxLThxY2pqbnFtM2E1NjhwMG05YWxhanYya2FhNTE0am90LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiODQ1MTYxMjIxMjUxLThxY2pqbnFtM2E1NjhwMG05YWxhanYya2FhNTE0am90LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTE1MzEwMzM0NDEyNzQ5NTY2MDEzIiwiZW1haWwiOiJqdXN0eW5hLmxpc29rQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiUjFNZVFodXZFSVFOTHFBWHZMSzFHUSIsIm5hbWUiOiJKdXN0eW5hIExpc29rIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tbDlMSEdteGptWmcvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQU1adXVjbDNqYjg4TGRZM2s4LUVRTUxFYldsZVZRZXIwZy9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiSnVzdHluYSIsImZhbWlseV9uYW1lIjoiTGlzb2siLCJsb2NhbGUiOiJwbCIsImlhdCI6MTYwMTY0OTQyNiwiZXhwIjoxNjAxNjUzMDI2LCJqdGkiOiJkMmMxNDJjOGY0ODJhZDU4YjJiMjc2NzIxYjk1MTY0NDM2MTBmZTlmIn0.IshRAM7Nk3wonJdFKgLiJJRBqervSNPAk8sFAJBr8q0FoOzXOYX7T3-E7OpBfGa7pauIGxk6fBiKQKBmetePYvPof4tI4JE9X_23Tj2VB1G8mxoYj9ovrTSnS2Bktk59YEuJ-RNl1iMFM5VNTf677L3sUPnAy1UFr5RnhYoKfD4KF_MMUqk9YLAvYcAdWCHLe41WlmHhbVvjVE78PUqC1iwCtMVZIKVVbNtfhaJjsuBfvut3cjAjY2rqw_z20wGvgE3hNWnOh5IvCiGDLBqDOnn4a9K5XwL4BlOqcGDgpDrhA10QfX7Hf8Mdpl5nqm0e14Dtis-AevZXYoAr98T0_g";
    private final String invalidToken = "gfdes";
    private final String endPointUrlVideos = "/api/v1/youtube/videos";
    private final String endPointUrlChannels = "/api/v1/youtube/channels";

    private YouTubeRatingDto dto;
    private String jsonHeader;
    private User user;


    @BeforeEach
    public void createYoutubeRequestDto() throws GeneralSecurityException, IOException {
        GoogleIdToken googleIdToken = tokenVerifier.verify(googleIdTokenString);
        user = userUtils.insertUserInDatabase(googleIdTokenString, googleIdToken, accessToken);
        dto = new YouTubeRatingDto(Rating.LIKE);
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
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlVideos, jsonHeader, dto))
                .andExpect(status().isOk())
                .andExpect(YouTubeEntityVerifier::assertResponseVideosNotNull);
    }


    @Test
    void getRatedVideos_whenRequestValid_RatingDislike() throws Exception {
        //given
        assertFalse(userVideoRepository.existsByUserId(user.getId()));
        YouTubeRatingDto dto = new YouTubeRatingDto(Rating.DISLIKE);
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlVideos, jsonHeader, dto))
                .andExpect(status().isOk())
                .andExpect(YouTubeEntityVerifier::assertResponseVideosNotNull);
    }


    @Test
    void getRatedVideos_whenNullRating() throws Exception {
        //given
        YouTubeRatingDto badRatingDto = new YouTubeRatingDto(null);
        when(tokenService.getAccessToken())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlVideos, jsonHeader, badRatingDto))
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
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlVideos, jsonHeader, dto))
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
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlChannels, jsonHeader, dto))
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
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlChannels, jsonHeader, dto))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }
}