package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.VideoUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jlisok.youtube_activity_manager.testutils.ChannelAndSubscriptionUtils.createRandomYouTubeChannelList;
import static com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier.assertListOfVideosEqual;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoServiceImplementationTest {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private VideoServiceImplementation service;

    private User user;
    private List<Channel> channels;
    private List<VideoCategory> videoCategories;
    private List<com.google.api.services.youtube.model.Channel> youtubeChannels;

    @BeforeAll
    void prepareInitialConditions() throws RegistrationException {
        youtubeChannels = createRandomYouTubeChannelList(20);
        user = userUtils.createUser(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        channels = youtubeChannels
                .stream()
                .map(ytChannel -> EntityCreator
                        .createChannel(ytChannel.getId(),
                                       ytChannel.getSnippet(),
                                       ytChannel.getStatistics(),
                                       ytChannel.getContentOwnerDetails(),
                                       Collections.singleton(user)))
                .collect(Collectors.toList());
        videoCategories = VideoUtils.createRandomListOfVideoCategories(20);
    }

    @ParameterizedTest
    @MethodSource("exampleYouTubeLists")
    void insertVideos_whenNewVideos(List<com.google.api.services.youtube.model.Video> youTubeVideoList) {
        //given //when
        List<Video> actualList = service.createVideos(youTubeVideoList, channels, videoCategories);

        //then
        assertListOfVideosEqual(youTubeVideoList, actualList);
    }


    Stream<Arguments> exampleYouTubeLists() {
        return Stream.of(
                Arguments.arguments(VideoUtils.createYouTubeVideoListGivenChannelsAndVideoCategory(0, youtubeChannels, videoCategories)),
                Arguments.arguments(VideoUtils.createYouTubeVideoListGivenChannelsAndVideoCategory(1, youtubeChannels, videoCategories)),
                Arguments.arguments(VideoUtils.createYouTubeVideoListGivenChannelsAndVideoCategory(5, youtubeChannels, videoCategories)),
                Arguments.arguments(VideoUtils.createYouTubeVideoListGivenChannelsAndVideoCategory(100, youtubeChannels, videoCategories))
        );
    }
}