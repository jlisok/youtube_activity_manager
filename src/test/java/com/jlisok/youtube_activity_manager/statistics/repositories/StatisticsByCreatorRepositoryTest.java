package com.jlisok.youtube_activity_manager.statistics.repositories;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCreator;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeActivityUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.utils.MapCreator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatisticsByCreatorRepositoryTest implements TestProfile {

    @Autowired
    private StatisticsByCreatorRepository repository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private YouTubeActivityUtils utils;

    private User user;
    private List<Video> videos;
    private List<Channel> channelsInVideos;
    private Map<String, Channel> channelMap;
    private int videosSize;


    @BeforeEach
    @Transactional
    void createInitialConditions() throws RegistrationException {
        user = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        videos = utils.insertUsersYouTubeActivity(user);
        videosSize = videos.size();
        channelsInVideos = videos
                .stream()
                .map(Video::getChannel)
                .distinct()
                .collect(Collectors.toList());
        channelMap = MapCreator.toMap(channelsInVideos, Channel::getTitle, Function.identity());
    }


    @Test
    @Transactional
    void groupByCreator_whenNoContentRelatedToUser() throws RegistrationException {
        //given //when
        User userNoContent = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        List<StatisticsByCreator> stats = repository.groupByCreator(userNoContent.getId());

        //then
        Assertions.assertTrue(stats.isEmpty());
    }


    @Test
    @Transactional
    void groupByCreator_whenUserWatchedContent() {
        //given //when
        List<StatisticsByCreator> stats = repository.groupByCreator(user.getId());
        Long sumNumberVideos = stats
                .stream()
                .map(StatisticsByCreator::getNumberVideos)
                .reduce(0L, Long::sum);

        //then
        Assertions.assertEquals(sumNumberVideos, videos.size());
        Assertions.assertEquals(channelsInVideos.size(), stats.size());
        stats.forEach(stat -> {
            Assertions.assertTrue(channelMap.containsKey(stat.getCreatorName()));
            Assertions.assertTrue(stat.getNumberVideos() <= videosSize);
        });
    }


    @Test
    @Transactional
    void groupByCreator_whenUserWatchedContentAndOtherContentPresentInDatabase() throws RegistrationException {
        //given //when
        createInitialConditions(); // add new user and set of youtube activity entities on top of the existing records
        List<StatisticsByCreator> stats = repository.groupByCreator(user.getId());
        Long sumNumberVideos = stats
                .stream()
                .map(StatisticsByCreator::getNumberVideos)
                .reduce(0L, Long::sum);

        //then
        Assertions.assertEquals(sumNumberVideos, videos.size());

        Assertions.assertEquals(channelsInVideos.size(), stats.size());
        stats.forEach(stat -> {
            Assertions.assertTrue(channelMap.containsKey(stat.getCreatorName()));
            Assertions.assertTrue(stat.getNumberVideos() <= videosSize);
        });
    }
}