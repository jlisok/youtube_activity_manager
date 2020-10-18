package com.jlisok.youtube_activity_manager.statistics.controllers;

import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.*;
import com.jlisok.youtube_activity_manager.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
    private MockMvc mockMvc;

    private final String byCreatorEndPoint = "/api/v1/statistics/creator";
    private final String byCategoryEndPoint = "/api/v1/statistics/category";

    private String jsonHeader;

    @BeforeEach
    @Transactional
    void createInitialConditions() throws RegistrationException {
        User user = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        youTubeActivityUtils.insertUsersYouTubeActivity(user);
        jsonHeader = authenticationUtils.createRequestAuthenticationHeader(user.getId().toString());
    }


    @Test
    @Transactional
    void getStatsGroupedByCategory() throws Exception {
        //given //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(byCategoryEndPoint, jsonHeader))
                .andExpect(status().isOk())
                .andExpect(YouTubeEntityVerifier::assertStatsByCategoryDtoNotNull);
    }


    @Test
    @Transactional
    void getStatsGroupedByCreator() throws Exception {
        //given //when //then
        mockMvc
                .perform(mvcRequestBuilder.setBasicGetRequest(byCreatorEndPoint, jsonHeader))
                .andExpect(status().isOk())
                .andExpect(YouTubeEntityVerifier::assertStatsByCreatorDtoNotNull);
    }
}