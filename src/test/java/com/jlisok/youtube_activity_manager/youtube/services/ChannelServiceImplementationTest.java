package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
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

import java.util.List;
import java.util.stream.Stream;

import static com.jlisok.youtube_activity_manager.testutils.ChannelAndSubscriptionUtils.createRandomYouTubeChannelList;
import static com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier.assertListOfChannelsEqual;
import static org.mockito.Mockito.when;

@SpringBootTest
class ChannelServiceImplementationTest implements TestProfile {

    @Autowired
    private ChannelService service;

    @Autowired
    private UserUtils utils;

    @MockBean
    private UserFetcher fetcher;

    @ParameterizedTest
    @MethodSource("exampleYouTubeChannels")
    void createListOfChannels(List<com.google.api.services.youtube.model.Channel> youtubeChannelList) throws RegistrationException {
        //given //when
        User user = utils.createUserWithDataFromToken(utils.createRandomEmail(), utils.createRandomPassword());

        when(fetcher.fetchUser(user.getId()))
                .thenReturn(user);

        List<Channel> channelList = service.createChannels(youtubeChannelList, user.getId());


        //then
        channelList.forEach(YouTubeEntityVerifier::assertChannelNotEmpty);
        assertListOfChannelsEqual(youtubeChannelList, channelList);
    }


    static Stream<Arguments> exampleYouTubeChannels() {
        return Stream.of(
                //Arguments.arguments(createRandomYouTubeChannelList(0)),
                Arguments.arguments(createRandomYouTubeChannelList(1)),
                Arguments.arguments(createRandomYouTubeChannelList(5)),
                Arguments.arguments(createRandomYouTubeChannelList(10))
        );
    }
}