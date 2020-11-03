package com.jlisok.youtube_activity_manager.youtube.controllers;

import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import com.jlisok.youtube_activity_manager.testutils.*;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class YouTubeControllerTest implements TestProfile {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private MockMvcBasicRequestBuilder mvcRequestBuilder;

    @Autowired
    private UserVideoRepository userVideoRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private SynchronizationRepository synchronizationRepository;

    @Autowired
    private AuthenticationUtils utils;

    @Autowired
    private MockMvc mockMvc;

    private final String endPointUrlVideos = "/api/v1/youtube/videos";
    private final String endPointUrlChannels = "/api/v1/youtube/channels";
    private final String parameterName = "rating";

    private String jsonHeader;
    private User user;
    private User otherUser;
    private UUID id;
    private Instant now;

    @BeforeEach
    @Transactional
    public void createYoutubeRequestDto() throws RegistrationException {
        user = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        otherUser = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        insertUserVideosInDatabase(user, Rating.LIKE);
        insertUserVideosInDatabase(user, Rating.DISLIKE);
        insertChannelsInDatabase(user);
        jsonHeader = utils.createRequestAuthenticationHeader(user.getId().toString(), true);
        id = UUID.randomUUID();
        now = Instant.now();
    }


    @Test
    @Transactional
    void getRatedVideos_whenRequestValid_RatingLike() throws Exception {
        //given
        var statusFirst = new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now.minus(Duration.ofMinutes(30)), user);
        var statusLast = new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now, user);
        synchronizationRepository.saveAll(List.of(statusFirst, statusLast));

        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setGetRequestWithParams(endPointUrlVideos, jsonHeader, parameterName, Rating.LIKE
                        .toString()))
                .andExpect(status().isOk())
                .andExpect(result -> YouTubeEntityVerifier.assertUserActivityDtoVideoNotNull(result, statusLast.getState(), statusLast
                        .getCreatedAt()));
    }


    @Test
    @Transactional
    void getRatedVideos_whenRequestValid_RatingDislike() throws Exception {
        //given
        var statusThisUser = new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now.minus(Duration.ofMinutes(30)), user);
        var statusOtherUser = new SynchronizationStatus(id, SynchronizationState.FAILED, now, otherUser);
        synchronizationRepository.saveAll(List.of(statusOtherUser, statusThisUser));

        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setGetRequestWithParams(endPointUrlVideos, jsonHeader, parameterName, Rating.DISLIKE
                        .toString()))
                .andExpect(status().isOk())
                .andExpect(result -> YouTubeEntityVerifier.assertUserActivityDtoVideoNotNull(result, statusThisUser.getState(), statusThisUser
                        .getCreatedAt()));
    }


    @Test
    void getRatedVideos_whenNullRating() throws Exception {
        //given //when //then
        mockMvc
                .perform(mvcRequestBuilder.setGetRequestWithParams(endPointUrlVideos, jsonHeader, parameterName, null))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BindException));
    }


    @Test
    void getSubscribedChannels() throws Exception {
        //given
        var emptyStatus = new SynchronizationStatus();

        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(endPointUrlChannels, jsonHeader))
                .andExpect(status().isOk())
                .andExpect(result -> YouTubeEntityVerifier.assertUserActivityDtoChannelNotNull(result, emptyStatus.getState(), emptyStatus
                        .getCreatedAt()));
    }


    private void insertUserVideosInDatabase(User user, Rating rating) {
        var videos = VideoUtils.createRandomListOfVideos(10);
        var userVideos = VideoUtils.createListOfUserVideos(videos, user, rating);
        userVideoRepository.saveAll(userVideos);
        userVideoRepository.flush();
    }


    private void insertChannelsInDatabase(User user) {
        var channels = ChannelAndSubscriptionUtils.createRandomListOfChannels(10, user);
        channelRepository.saveAll(channels);
        channelRepository.flush();
        Assertions.assertEquals(channels.size(), channelRepository.findByUsers_Id(user.getId()).size());
    }
}