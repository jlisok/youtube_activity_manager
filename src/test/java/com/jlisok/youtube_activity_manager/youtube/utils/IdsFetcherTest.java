package com.jlisok.youtube_activity_manager.youtube.utils;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoCategory;
import com.jlisok.youtube_activity_manager.testutils.ChannelAndSubscriptionUtils;
import com.jlisok.youtube_activity_manager.testutils.VideoUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IdsFetcherTest {

    private static List<Video> videos;
    private static List<Channel> channels;
    private static List<VideoCategory> videoCategories;

    @BeforeAll
    static void createInitialConditions() {
        videoCategories = VideoUtils.createRandomListOfYouTubeVideoCategories(50);
        videos = VideoUtils.createYouTubeVideoList(120);
        channels = ChannelAndSubscriptionUtils.createRandomYouTubeChannelList(100);
    }


    @ParameterizedTest
    @MethodSource("exampleCollections")
    <T> void getIdsFrom(IdsFunction<T> idsFunction) {
        //given
        List<T> collection = idsFunction.getList();
        // when
        List<String> ids = IdsFetcher.getIdsFrom(collection, idsFunction.toIds());

        //then
        assertNotNull(ids);
        assertEquals(collection.size(), ids.size());
    }


    static Stream<Arguments> exampleCollections() {
        return Stream.of(
                Arguments.of(new IdsFunction<>(videos, video -> video.getSnippet().getCategoryId())),
                Arguments.of(new IdsFunction<>(videos, Video::getId)),
                Arguments.of(new IdsFunction<>(videoCategories, VideoCategory::getId)),
                Arguments.of(new IdsFunction<>(channels, Channel::getId))
        );
    }


    static class IdsFunction<T> {

        private final Function<T, String> toIdsFunction;
        private final List<T> list;

        IdsFunction(List<T> list, Function<T, String> toIds) {
            this.toIdsFunction = toIds;
            this.list = list;
        }

        Function<T, String> toIds() {
            return toIdsFunction;
        }

        List<T> getList() {
            return list;
        }
    }
}