package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;
import com.jlisok.youtube_activity_manager.youtube.utils.AccessTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.setAuthenticationInContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Disabled("test methods require refreshed access token, which is now not implemented. In due time, this test class should be run manually.")
class YouTubeServiceImplementationTest {

    @Autowired
    private YouTubeServiceImplementation service;

    @Autowired
    private UserUtils userUtils;

    @MockBean
    private AccessTokenService accessTokenService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserVideoRepository userVideoRepository;


    private final String accessToken = "ya29.a0AfH6SMC38cOOPyXX9Lkt4L54qcmh3cgBcKYlDz3hRUuV15Yqo145PryyxQmIBH1bo9XEriNquwaB5Acnkn47-Bdgb36toxQFYhA8UYz6Ahv4T4TfAPm5zBuDScVGEdmyjlJehQM4p6j5ZLBGo-9H3idshTc2xjSh4ISP";
    private final String validGoogleIdToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjJjNmZhNmY1OTUwYTdjZTQ2NWZjZjI0N2FhMGIwOTQ4MjhhYzk1MmMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiODQ1MTYxMjIxMjUxLThxY2pqbnFtM2E1NjhwMG05YWxhanYya2FhNTE0am90LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiODQ1MTYxMjIxMjUxLThxY2pqbnFtM2E1NjhwMG05YWxhanYya2FhNTE0am90LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTE1MzEwMzM0NDEyNzQ5NTY2MDEzIiwiZW1haWwiOiJqdXN0eW5hLmxpc29rQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiYTl4a2oyY3dyQW9uemhlaVFjTTNlUSIsIm5hbWUiOiJKdXN0eW5hIExpc29rIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tbDlMSEdteGptWmcvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQU1adXVjbDNqYjg4TGRZM2s4LUVRTUxFYldsZVZRZXIwZy9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiSnVzdHluYSIsImZhbWlseV9uYW1lIjoiTGlzb2siLCJsb2NhbGUiOiJwbCIsImlhdCI6MTYwMDg1OTA0OCwiZXhwIjoxNjAwODYyNjQ4LCJqdGkiOiIyNjg5Y2VkYjcxYmM4ODhkZjViNzk2NmYzMjQ0YjY5YjUwNDk0MjA1In0.cTie0ibKc5KhYP1OzhFDF79yt_F3lxE2ZIkaGa_pIJIqsAHPoC8c4Wx6Dvuv8boCigYGAFv6j-28EwxEZ5q_DzVK1aQ4Sb4fNa6NF1SUUYQ6KQ2on95vYGDWBVPyCPASK0gxKFrXR9JGBHBKqHpCr3dCohIOJyBE3u7bN-WZfy4mmK0cRIuYUcMXo6k-egx5nUrxug_gjpddVyIGA77KqpQgShQKjCd1paZO5uE463JdrJhbaEoLcYHwnWsqZsTaG4McxDgMqjsIITeDsVVL3hV1Lwsu98C7svJBXYBmTbVE4yJrFkmmFf2ELQSV8zHC9KGyZvPMO2pkfQ9ATeLDIg";
    private final String invalidToken = "gfdes";

    private YouTubeRatingDto dto;
    private User user;
    private UUID id;

    @BeforeEach
    void createDto() {
        dto = new YouTubeRatingDto(Rating.LIKE);
        id = UUID.randomUUID();
        user = userUtils.createUser(id, validGoogleIdToken, accessToken);
        setAuthenticationInContext(validGoogleIdToken, id);
    }


    @Test
    void listRatedVideos_whenTokenInvalid() {
        //given
        when(accessTokenService.getAccessToken())
                .thenReturn(invalidToken);

        //when //then
        assertThrows(GoogleJsonResponseException.class, () -> service.listRatedVideos(dto));
    }


    @Test
    void listRatedVideos_whenDtoRatingDislike() throws Exception {
        //given
        YouTubeRatingDto dislikeDto = new YouTubeRatingDto(Rating.DISLIKE);
        when(accessTokenService.getAccessToken())
                .thenReturn(accessToken);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(userVideoRepository.saveAll(ArgumentMatchers.anyList()))
                .thenReturn(new ArrayList<>());

        //when
        List<Video> videoList = service.listRatedVideos(dislikeDto);

        //then
        verify(userVideoRepository).saveAll(ArgumentMatchers.anyList());
        videoList.forEach(YouTubeApiUtils::assertVideoNotEmpty);
    }


    @Test
    void listRatedVideos_whenDtoRatingLike() throws Exception {
        //given
        when(accessTokenService.getAccessToken())
                .thenReturn(accessToken);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(userVideoRepository.saveAll(ArgumentMatchers.anyList()))
                .thenReturn(new ArrayList<>());

        //when
        List<Video> videoList = service.listRatedVideos(dto);

        //then
        verify(userVideoRepository).saveAll(ArgumentMatchers.anyList());
        videoList.forEach(YouTubeApiUtils::assertVideoNotEmpty);
    }


    //TODO: method flow to be changed in due time
    @Test
    void listSubscribedChannels_whenTokenValid() throws Exception {
        //given
        when(accessTokenService.getAccessToken())
                .thenReturn(accessToken);

        //when
        List<Subscription> response = service.listSubscribedChannels();

        //then
        assertNotNull(response);
        for (Subscription sub : response) {
            assertNotNull(sub.getSnippet());
            assertNotNull(sub.getContentDetails());
        }
    }

    //TODO: method flow to be changed in due time
    @Test
    void listSubscribedChannels_whenTokenInvalid() {
        //given
        when(accessTokenService.getAccessToken())
                .thenReturn(invalidToken);

        //when //then
        assertThrows(GoogleJsonResponseException.class, () -> service.listSubscribedChannels());
    }
}