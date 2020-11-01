package com.jlisok.youtube_activity_manager.statistics.controllers;

import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import com.jlisok.youtube_activity_manager.testutils.*;
import com.jlisok.youtube_activity_manager.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatisticsControllerTest implements TestProfile {

    @Autowired
    private MockMvcBasicRequestBuilder mvcRequestBuilder;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private YouTubeActivityUtils youTubeActivityUtils;

    @Autowired
    private AuthenticationUtils authenticationUtils;

    @Autowired
    private SynchronizationRepository synchronizationRepository;

    @Autowired
    private MockMvc mockMvc;

    private final String byCreatorEndPoint = "/api/v1/statistics/creator";
    private final String byCategoryEndPoint = "/api/v1/statistics/category";

    private String jsonHeader;
    private User user;
    private User otherUser;
    private UUID id;
    private Instant now;

    @BeforeEach
    @Transactional
    void createInitialConditions() throws RegistrationException {
        user = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        youTubeActivityUtils.insertUsersYouTubeActivity(user);
        jsonHeader = authenticationUtils.createRequestAuthenticationHeader(user.getId().toString(), true);
        id = UUID.randomUUID();
        now = Instant.now();
        otherUser = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());

    }


    @Test
    @Transactional
    void getStatsGroupedByCategory_andMoreThanOneStatus() throws Exception {
        //given
        var statusFirst = new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now.minus(Duration.ofMinutes(30)), user);
        var statusLast = new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now, user);
        synchronizationRepository.saveAll(List.of(statusFirst, statusLast));

        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(byCategoryEndPoint, jsonHeader))
                .andExpect(status().isOk())
                .andExpect(result -> YouTubeEntityVerifier.assertStatsByCategoryDtoNotNull(result, statusLast.getState(), statusLast
                        .getCreatedAt()));
    }


    @Test
    @Transactional
    void getStatsGroupedByCategory_andStatusesForUsers() throws Exception {
        //given
        var statusThisUser = new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now.minus(Duration.ofMinutes(30)), user);
        var statusOtherUser = new SynchronizationStatus(id, SynchronizationState.FAILED, now, otherUser);
        synchronizationRepository.saveAll(List.of(statusOtherUser, statusThisUser));

        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(byCategoryEndPoint, jsonHeader))
                .andExpect(status().isOk())
                .andExpect(result -> YouTubeEntityVerifier.assertStatsByCategoryDtoNotNull(result, statusThisUser.getState(), statusThisUser
                        .getCreatedAt()));
    }


    @Test
    @Transactional
    void getStatsGroupedByCreator_whenStatusEmpty() throws Exception {
        //given
        var emptyStatus = new SynchronizationStatus();


        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(byCreatorEndPoint, jsonHeader))
                .andExpect(status().isOk())
                .andExpect(result -> YouTubeEntityVerifier.assertStatsByCreatorDtoNotNull(result, emptyStatus.getState(), emptyStatus.getCreatedAt()));
    }
}