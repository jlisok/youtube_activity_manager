package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.ChannelAndSubscriptionUtils;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.youtube.utils.IdsFetcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ChannelDatabaseServiceImplementationTest implements TestProfile {

    @Autowired
    private UserUtils userUtils;

    @Captor
    private ArgumentCaptor<List<Channel>> captor;

    @MockBean
    private ChannelRepository repository;

    @Autowired
    private ChannelDatabaseService databaseService;

    @SuppressWarnings("unchecked")
    private static final Answer<List<Channel>> interceptChannels = invocation -> (List<Channel>) invocation.getArguments()[0];

    private User user;
    private List<com.jlisok.youtube_activity_manager.channels.models.Channel> channels;
    List<String> channelIds;


    @BeforeEach
    void createBoundaryConditions() throws RegistrationException {
        user = userUtils.createUserWithDataFromToken(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        channels = ChannelAndSubscriptionUtils.createRandomListOfChannels(2, user);
        channelIds = IdsFetcher.getIdsFrom(channels, Channel::getYouTubeChannelId);
    }


    @Test
    void updateChannelsInDatabase_whenAllNewChannels() {
        //given
        when(repository.findAllByYouTubeChannelIdIn(channelIds))
                .thenReturn(Collections.emptyList());

        when(repository.saveAll(channels))
                .thenAnswer(interceptChannels);

        // when
        databaseService.updateChannelsInDatabase(channels, user.getId());

        //then
        verify(repository).saveAll(captor.capture());
        captor.getValue().forEach(channel -> Assertions.assertEquals(channel.getCreatedAt()
                                                                            .compareTo(channel.getModifiedAt()), 0));
    }


    @Test
    void updateChannelsInDatabase_whenAllChannelsToUpdate() throws RegistrationException {
        //given
        User userInDatabase = userUtils.createUserWithDataFromToken(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        List<Channel> repositoryChannels = ChannelAndSubscriptionUtils.copyOfMinus30MinutesCreatedAt(channels, userInDatabase);

        when(repository.findAllByYouTubeChannelIdIn(channelIds))
                .thenReturn(repositoryChannels);

        when(repository.saveAll(anyList()))
                .thenAnswer(interceptChannels);

        // when
        databaseService.updateChannelsInDatabase(channels, user.getId());

        //then
        verify(repository).saveAll(captor.capture());
        List<Channel> actualChannels = captor.getValue();
        IntStream.range(0, actualChannels.size())
                 .forEach(i -> {
                     Channel dbChannel = repositoryChannels.get(i);
                     Channel actualChannel = actualChannels.get(i);
                     Assertions.assertEquals(dbChannel.getCreatedAt(), actualChannel.getCreatedAt());
                     Assertions.assertNotEquals(dbChannel.getModifiedAt(), actualChannel.getModifiedAt());
                     Assertions.assertEquals(dbChannel.getId(), actualChannel.getId());
                     Assertions.assertEquals(dbChannel.getYouTubeChannelId(), actualChannel.getYouTubeChannelId());
                 });
    }
}