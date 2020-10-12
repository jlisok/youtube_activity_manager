package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils.createRandomSubscriptionList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class YouTubeChannelIdServiceImplementationTest {

    @Autowired
    private YouTubeChannelIdService service;

    @ParameterizedTest
    @MethodSource("exampleYouTubeChannels")
    void fetchChannelIds(List<Subscription> youtubeSubscriptionList) {
        //given //when
        List<String> channelIds = service.getChannelIdFromSubscriptions(youtubeSubscriptionList);

        //then
        IntStream.range(0, channelIds.size())
                 .forEach(i -> {
                     assertNotNull(channelIds.get(i));
                     assertEquals(youtubeSubscriptionList.get(i).getSnippet().getResourceId().getChannelId(), channelIds.get(i));
                 });
    }


    static Stream<Arguments> exampleYouTubeChannels() {
        return Stream.of(
                Arguments.arguments(createRandomSubscriptionList(0)),
                Arguments.arguments(createRandomSubscriptionList(1)),
                Arguments.arguments(createRandomSubscriptionList(5)),
                Arguments.arguments(createRandomSubscriptionList(10))
        );
    }
}