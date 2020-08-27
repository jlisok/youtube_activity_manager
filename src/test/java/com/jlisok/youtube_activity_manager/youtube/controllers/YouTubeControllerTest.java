package com.jlisok.youtube_activity_manager.youtube.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.jlisok.youtube_activity_manager.testutils.AuthenticationUtils;
import com.jlisok.youtube_activity_manager.testutils.MockMvcYouTubeListResponse;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeListDto;
import com.jlisok.youtube_activity_manager.youtube.utils.AccessTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("test methods require refreshed access token, which is now not implemented. In due time, this test class should be run manually.")
class YouTubeControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationUtils utils;

    @MockBean
    private AccessTokenService tokenService;

    @Autowired
    private MockMvc mockMvc;

    private final String accessToken = "ya29.a0AfH6SMDaGpCVmAmP8T49IFuWhttGFzOK0dKmDYm5jyMuxGWgsc7GqZBiSTeZTZYCYPU6kQ9G9bec9IsYw-fD14Y-53t8vY7PWycOUXmewL7zWMxGSx0LJQSbmEfCNJpjISAvA-8nOz7xW2j7FmrJdDAHr2UAr3fatI3t";
    private final String invalidToken = "gfdes";
    private YouTubeListDto dto;
    private String jsonHeader;

    @BeforeEach
    public void createYoutubeRequestDto() {
        String requestParts = "snippet, contentDetails";
        dto = new YouTubeListDto(requestParts, "like");
        String userId = "16b5d624-cc36-403c-835e-d1988c2410a8";
        jsonHeader = utils.createRequestAuthenticationHeader(userId);
    }


    @Test
    void getLikedVideos_whenRequestValid() throws Exception {
        //given
        when(tokenService.get())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/youtube/videos")
                                .header(HttpHeaders.AUTHORIZATION, jsonHeader)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcYouTubeListResponse::assertResponseListNotNull);
    }

    @Test
    void getDislikedVideos_whenRequestValid() throws Exception {
        //given
        YouTubeListDto dislikedDto = new YouTubeListDto("snippet, contentDetails", "dislike");

        when(tokenService.get())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/youtube/videos")
                                .header(HttpHeaders.AUTHORIZATION, jsonHeader)
                                .content(objectMapper.writeValueAsString(dislikedDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcYouTubeListResponse::assertResponseListNotNull);
    }

    @Test
    void getRatedVideos_whenTokenInvalid() throws Exception {
        //given

        when(tokenService.get())
                .thenReturn(invalidToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/youtube/videos")
                                .header(HttpHeaders.AUTHORIZATION, jsonHeader)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }


    @Test
    void getRatedVideos_badRating() throws Exception {
        //given
        YouTubeListDto badRatingDto = new YouTubeListDto("snippet, contentDetails", "blahblah");
        when(tokenService.get())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/youtube/videos")
                                .header(HttpHeaders.AUTHORIZATION, jsonHeader)
                                .content(objectMapper.writeValueAsString(badRatingDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }


    @Test
    void getRatedVideos_nullRating() throws Exception {
        //given
        YouTubeListDto badRatingDto = new YouTubeListDto("snippet, contentDetails", null);
        when(tokenService.get())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/youtube/videos")
                                .header(HttpHeaders.AUTHORIZATION, jsonHeader)
                                .content(objectMapper.writeValueAsString(badRatingDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }


    @Test
    void getSubscribedChannels() throws Exception {
        //given
        when(tokenService.get())
                .thenReturn(accessToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/youtube/channels")
                                .header(HttpHeaders.AUTHORIZATION, jsonHeader)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcYouTubeListResponse::assertResponseListNotNull);
    }


    @Test
    void getSubscribedChannels_whenTokenInvalid() throws Exception {
        //given
        when(tokenService.get())
                .thenReturn(invalidToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/youtube/channels")
                                .header(HttpHeaders.AUTHORIZATION, jsonHeader)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof GoogleJsonResponseException));
    }
}