package com.jlisok.youtube_activity_manager.synchronization.services;

import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import com.jlisok.youtube_activity_manager.testutils.TestChannelRepository;
import com.jlisok.youtube_activity_manager.testutils.TestUserVideoRepository;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@Disabled("YouTube API requires valid access token to download data, which needs to be typed manually in due time")
class YouTubeDataSynchronizationServiceAutowiredYouTubeApiTest {

    @Autowired
    private YouTubeDataSynchronizationServiceImplementation youTubeDataSynchronizationService;

    @Autowired
    private SynchronizationRepository synchronizationRepository;

    @Autowired
    private UserVideoRepository userVideoRepository;

    @Autowired
    private TestUserVideoRepository testUserVideoRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestChannelRepository testChannelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserUtils userUtils;

    private final String accessToken = "dummyAccessTokenDummyAccessToken";

    private User user;

    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        user = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());
    }


    /**
     * Async methods do not work with @transactional. Must remove all added entities from database manually
     */
    @AfterEach
    void removeDataFromDatabase() {
        synchronizationRepository.deleteByUserId(user.getId());
        testUserVideoRepository.deleteAllByUserId(user.getId());
        testChannelRepository.deleteAllByUsers_Id(user.getId());
        userRepository.delete(user);
    }


    @Test
    void synchronize_whenAccessTokenInvalid() {
        //given
        String invalidToken = "expiredDummyAccessTokenExpiredDummyAccessToken";
        UUID synchronizationId = UUID.randomUUID();

        //when
        youTubeDataSynchronizationService.synchronizeYouTubeData(synchronizationId, invalidToken, user
                .getId()).join();

        //then
        SynchronizationState actualState = synchronizationRepository
                .findByUserId(user.getId())
                .map(SynchronizationStatus::getState)
                .orElse(null);

        Assertions.assertNotNull(actualState);
        Assertions.assertEquals(SynchronizationState.FAILED, actualState);
    }


    @Test
    void synchronize_whenSubscribedChannelsFetchedFromYouTubeClient() {
        //given
        UUID synchronizationId = UUID.randomUUID();

        //when
        youTubeDataSynchronizationService.synchronizeYouTubeData(synchronizationId, accessToken, user
                .getId()).join();

        //then
        SynchronizationState actualState = synchronizationRepository
                .findByUserId(user.getId())
                .map(SynchronizationStatus::getState)
                .orElse(null);

        var channels = channelRepository.findByUsers_Id(user.getId());
        var userVideos = userVideoRepository.findByUserId(user.getId());

        Assertions.assertFalse(channels.isEmpty());
        Assertions.assertFalse(userVideos.isEmpty());

        Assertions.assertNotNull(actualState);
        Assertions.assertEquals(SynchronizationState.SUCCEEDED, actualState);
    }
}
