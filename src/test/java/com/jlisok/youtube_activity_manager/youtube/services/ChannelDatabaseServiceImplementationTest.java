package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
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

import static com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils.createRandomListOfChannels;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ChannelDatabaseServiceImplementationTest {

    @Autowired
    UserUtils userUtils;

    @Captor
    private ArgumentCaptor<List<Channel>> captor;

    @MockBean
    ChannelRepository repository;

    @Autowired
    private ChannelDatabaseService daoService;

    @SuppressWarnings("unchecked")
    private static final Answer<List<Channel>> interceptChannels = invocation -> (List<Channel>) invocation.getArguments()[0];

    private User user;
    private List<com.jlisok.youtube_activity_manager.channels.models.Channel> channels;


    @BeforeEach
    void createBoundaryConditions() throws RegistrationException {
        String email = userUtils.createRandomEmail();
        String password = userUtils.createRandomPassword();
        user = userUtils.createUser(email, password);
        channels = createRandomListOfChannels(10, user);
    }


    @Test
    void updateChannelsInDatabase_whenAllNewChannels() {
        //given
        when(repository.findByUsers_Id(user.getId()))
                .thenReturn(Collections.emptyList());

        when(repository.saveAll(channels))
                .thenAnswer(interceptChannels);

        // when
        daoService.updateChannelsInDatabase(channels, user.getId());

        //then
        verify(repository).saveAll(captor.capture());
        captor.getValue().forEach(channel -> Assertions.assertEquals(channel.getCreatedAt(), channel.getModifiedAt()));
    }


    @Test
    void updateChannelsInDatabase_whenAllChannelsToUpdate() throws RegistrationException {
        //given
        User userInDatabase = userUtils.createUser(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        List<Channel> repositoryChannels = YouTubeApiUtils.copyOfMinus30MinutesCreatedAt(channels, userInDatabase);

        when(repository.findByUsers_Id(user.getId()))
                .thenReturn(repositoryChannels);

        when(repository.saveAll(anyList()))
                .thenAnswer(interceptChannels);

        // when
        daoService.updateChannelsInDatabase(channels, user.getId());

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
                 });
    }
}