package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeListDto;
import com.jlisok.youtube_activity_manager.youtube.utils.AccessTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Disabled("test methods require refreshed access token, which is now not implemented. In due time, this test class should be run manually.")
class YouTubeServiceImplementationTest {

    @Autowired
    private YouTubeServiceImplementation service;

    @MockBean
    private AccessTokenService accessTokenService;

    private final String token = "ya29.a0AfH6SMAdpifagDT2XObBj_p89D4X5HIL1_35JCMAy0J9xvOO28PVsNWN7vJxqmgN76ruDH39bSnj1T3srjcyratZzA9FUU3MfK-Kc1cAgyPjh3VTuONfhHiIU8HAv3tt_W8oU-p4ZH5y95NIs9j8b6bh4RC5xhOJdPp1";
    private YouTubeListDto dto;
    private final String invalidToken = "gfdes";

    @BeforeEach
    void createDto() {
        String requestParts = "snippet, contentDetails";
        dto = new YouTubeListDto(requestParts, "like");
    }


    @Test
    void listOfChannels_whenTokenValid() throws IOException, GeneralSecurityException {
        //given
        when(accessTokenService.get())
                .thenReturn(token);

        //when
        List<Subscription> response = service.listOfChannels(dto);

        //then
        assertNotNull(response);
        for (Subscription sub : response) {
            assertNotNull(sub.getSnippet());
            assertNotNull(sub.getContentDetails());
        }
    }


    @Test
    void listOfChannels_whenTokenInvalid() {
        //given
        when(accessTokenService.get())
                .thenReturn(invalidToken);

        //when //then
        assertThrows(GoogleJsonResponseException.class, () -> service.listOfChannels(dto));
    }


    @Test
    void listRatedVideos_Liked() throws IOException, GeneralSecurityException {
        //given
        when(accessTokenService.get())
                .thenReturn(token);

        //when
        List<Video> response = service.listRatedVideos(dto);

        //then
        assertNotNull(response);
        for (Video video : response) {
            assertNotNull(video.getSnippet());
            assertNotNull(video.getContentDetails());
        }
    }


    @Test
    void listRatedVideos_Disliked() throws IOException, GeneralSecurityException {
        //given
        YouTubeListDto dislikeRatingDto = new YouTubeListDto("snippet, contentDetails", "dislike");
        when(accessTokenService.get())
                .thenReturn(token);

        //when
        List<Video> response = service.listRatedVideos(dislikeRatingDto);

        //then
        assertNotNull(response);
        for (Video video : response) {
            assertNotNull(video.getSnippet());
            assertNotNull(video.getContentDetails());
        }
    }


    @Test
    void listRatedVideos_NoneRating() throws IOException, GeneralSecurityException {
        //given
        YouTubeListDto notAllowedRatingDto = new YouTubeListDto("snippet, contentDetails", "none");
        when(accessTokenService.get())
                .thenReturn(token);

        //when
        List<Video> response = service.listRatedVideos(notAllowedRatingDto);

        //then
        assertTrue(response.isEmpty());
    }


    @Test
    void listRatedVideos_NotAllowedRating() {
        //given
        YouTubeListDto notAllowedRatingDto = new YouTubeListDto("snippet, contentDetails", "blahblah");
        when(accessTokenService.get())
                .thenReturn(token);

        //then
        assertThrows(GoogleJsonResponseException.class, () -> service.listRatedVideos(notAllowedRatingDto));
    }


    @Test
    void listRatedVideos_NullRating() {
        //given
        YouTubeListDto nullRatingDto = new YouTubeListDto("snippet, contentDetails", null);
        when(accessTokenService.get())
                .thenReturn(token);

        //when //then
        assertThrows(GoogleJsonResponseException.class, () -> service.listRatedVideos(nullRatingDto));
    }


    @Test
    void listOfVideos_whenTokenInvalid() {
        //given
        when(accessTokenService.get())
                .thenReturn(invalidToken);

        //when //then
        assertThrows(GoogleJsonResponseException.class, () -> service.listRatedVideos(dto));
    }

}