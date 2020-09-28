package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.videos.models.Video;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils.createRandomYouTubeVideoList;
import static com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier.assertListOfVideosEqual;

@SpringBootTest
class VideoServiceImplementationTest {

    @Autowired
    private VideoServiceImplementation service;

    @ParameterizedTest
    @MethodSource("exampleYouTubeLists")
    void insertVideos_whenNewVideos(List<com.google.api.services.youtube.model.Video> youTubeVideoList) {
        //given //when
        List<Video> actualList = service.createVideos(youTubeVideoList);

        //then
        assertListOfVideosEqual(youTubeVideoList, actualList);
    }


    static Stream<Arguments> exampleYouTubeLists() {
        return Stream.of(
                Arguments.arguments(createRandomYouTubeVideoList(0)),
                Arguments.arguments(createRandomYouTubeVideoList(1)),
                Arguments.arguments(createRandomYouTubeVideoList(5)),
                Arguments.arguments(createRandomYouTubeVideoList(10))
        );
    }
}