package com.jlisok.youtube_activity_manager.statistics.repositories;

import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCategory;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeActivityUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.utils.MapCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatisticsByVideoCategoryRepositoryTest implements TestProfile {

    @Autowired
    private StatisticsByVideoCategoryRepository repository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private YouTubeActivityUtils utils;

    private User user;
    private List<VideoCategory> categoriesInVideos;
    private List<Video> videos;
    private Map<String, VideoCategory> categoryMap;
    private int videosSize;


    @BeforeEach
    @Transactional
    void createInitialConditions() throws RegistrationException {
        user = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        videos = utils.insertUsersYouTubeActivity(user);
        videosSize = videos.size();
        categoriesInVideos = videos
                .stream()
                .map(Video::getVideoCategory)
                .distinct()
                .collect(Collectors.toList());
        categoryMap = MapCreator.toMap(categoriesInVideos, VideoCategory::getCategoryName);
    }


    @Test
    @Transactional
    void groupByCreator_whenNoContentRelatedToUser() throws RegistrationException {
        //given //when
        User userNoContent = userUtils.insertUserInDatabase(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        List<StatisticsByCategory> stats = repository.groupByCategory(userNoContent.getId());

        //then
        Assertions.assertTrue(stats.isEmpty());
    }


    @Test
    @Transactional
    void groupByCreator_whenUserWatchedContent() {
        //given //when
        List<StatisticsByCategory> stats = repository.groupByCategory(user.getId());
        Long sumNumberVideos = stats
                .stream()
                .map(StatisticsByCategory::getNumberVideos)
                .reduce(0L, Long::sum);

        //then
        Assertions.assertEquals(sumNumberVideos, videos.size());
        Assertions.assertEquals(categoriesInVideos.size(), stats.size());
        stats.forEach(stat -> {
            Assertions.assertTrue(categoryMap.containsKey(stat.getCategoryName()));
            Assertions.assertTrue(stat.getNumberVideos() <= videosSize);
        });
    }


    @Test
    @Transactional
    void groupByCreator_whenUserWatchedContentAndOtherContentPresentInDatabase() throws RegistrationException {
        //given //when
        createInitialConditions(); // add new user and set of youtube activity entities on top of the existing records
        List<StatisticsByCategory> stats = repository.groupByCategory(user.getId());
        Long sumNumberVideos = stats
                .stream()
                .map(StatisticsByCategory::getNumberVideos)
                .reduce(0L, Long::sum);

        //then
        Assertions.assertEquals(sumNumberVideos, videos.size());
        Assertions.assertEquals(categoriesInVideos.size(), stats.size());
        stats.forEach(stat -> {
            Assertions.assertTrue(categoryMap.containsKey(stat.getCategoryName()));
            Assertions.assertTrue(stat.getNumberVideos() <= videosSize);
        });
    }
}