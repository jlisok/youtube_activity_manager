package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channel.models.Channel;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.utils.UserFetcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils.createRandomYouTubeChannelList;
import static com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier.assertListOfChannelsEqual;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class ChannelServiceImplementationTest {

    @Autowired
    private ChannelService service;

    @Autowired
    UserUtils utils;

    @MockBean
    private UserFetcher fetcher;

    @ParameterizedTest
    @MethodSource("exampleYouTubeChannels")
    void createListOfChannels(List<com.google.api.services.youtube.model.Channel> youtubeChannelList) throws RegistrationException {
        //given //when
        User user = utils.createUser(utils.createRandomEmail(), utils.createRandomPassword());
        List<Channel> channelList = service.createChannels(youtubeChannelList, user.getId());

        when(fetcher.fetchUser(user.getId())).thenReturn(user);

        //then
        channelList.forEach(YouTubeEntityVerifier::assertChannelNotEmpty);
        assertListOfChannelsEqual(youtubeChannelList, channelList);
    }


    static Stream<Arguments> exampleYouTubeChannels() {
        return Stream.of(
                Arguments.arguments(createRandomYouTubeChannelList(0)),
                Arguments.arguments(createRandomYouTubeChannelList(1)),
                Arguments.arguments(createRandomYouTubeChannelList(5)),
                Arguments.arguments(createRandomYouTubeChannelList(10))
        );
    }
}