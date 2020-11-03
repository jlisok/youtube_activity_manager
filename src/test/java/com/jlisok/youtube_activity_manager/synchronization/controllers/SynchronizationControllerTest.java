package com.jlisok.youtube_activity_manager.synchronization.controllers;

import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import com.jlisok.youtube_activity_manager.testutils.AuthenticationUtils;
import com.jlisok.youtube_activity_manager.testutils.MockMvcBasicRequestBuilder;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier;
import com.jlisok.youtube_activity_manager.users.models.User;
import org.junit.jupiter.api.Assertions;
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
class SynchronizationControllerTest {

    @Autowired
    private UserUtils utils;

    @Autowired
    private AuthenticationUtils authenticationUtils;

    @Autowired
    private MockMvcBasicRequestBuilder mvcRequestBuilder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SynchronizationRepository repository;

    private User user;
    private String jsonHeader;

    @BeforeEach
    @Transactional
    void createInitialConditions() throws RegistrationException {
        user = utils.insertUserInDatabase(utils.createRandomEmail(), utils.createRandomPassword());
        jsonHeader = authenticationUtils.createRequestAuthenticationHeader(user.getId().toString(), true);

    }


    @Test
    @Transactional
    void getTimestampOfLastSuccessfulSynchronization_WhenOneSync() throws Exception {
        //given
        SynchronizationStatus status = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.SUCCEEDED, Instant
                .now(), user);
        repository.save(status);

        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest("/api/v1/synchronization", jsonHeader))
                .andExpect(status().isOk())
                .andExpect(result -> YouTubeEntityVerifier.assertSynchronizationInstantEquals(result, status.getCreatedAt()));
    }


    @Test
    void getTimestampOfLastSuccessfulSynchronization_WhenNoSync() throws Exception {
        //given // when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest("/api/v1/synchronization", jsonHeader))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty()));
    }

    @Test
    @Transactional
    void getTimestampOfLastSuccessfulSynchronization_WhenOneSyncStatusOtherThanSucceeded() throws Exception {
        //given
        SynchronizationStatus status = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.IN_PROGRESS, Instant
                .now(), user);
        repository.save(status);

        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest("/api/v1/synchronization", jsonHeader))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty()));
    }

    @Test
    @Transactional
    void getTimestampOfLastSuccessfulSynchronization_WhenMoreSync() throws Exception {
        //given
        SynchronizationStatus latestStatus = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.SUCCEEDED, Instant
                .now(), user);
        SynchronizationStatus previousStatus = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.SUCCEEDED, Instant
                .now().minus(Duration.ofMinutes(30)), user);
        repository.saveAll(List.of(latestStatus, previousStatus));

        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest("/api/v1/synchronization", jsonHeader))
                .andExpect(status().isOk())
                .andExpect(result -> YouTubeEntityVerifier.assertSynchronizationInstantEquals(result, latestStatus.getCreatedAt()));
    }


    @Test
    @Transactional
    void getTimestampOfLastSuccessfulSynchronization_WhenOneSyncForRequestedUser() throws Exception {
        //given
        User otherUser = utils.insertUserInDatabase(utils.createRandomEmail(), utils.createRandomPassword());
        SynchronizationStatus status = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.SUCCEEDED, Instant
                .now(), user);
        SynchronizationStatus otherUserStatus = new SynchronizationStatus(UUID.randomUUID(), SynchronizationState.SUCCEEDED, Instant
                .now(), otherUser);
        repository.saveAll(List.of(status, otherUserStatus));

        // when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest("/api/v1/synchronization", jsonHeader))
                .andExpect(status().isOk())
                .andExpect(result -> YouTubeEntityVerifier.assertSynchronizationInstantEquals(result, status.getCreatedAt()));
    }

}