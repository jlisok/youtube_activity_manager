package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class YouTubeActivityUtils implements TestProfile {

    @Autowired
    private UserVideoRepository userVideoRepository;


    @Transactional
    public List<Video> insertUsersYouTubeActivity(User user) {
        int size = 10;
        var channels = ChannelAndSubscriptionUtils.createRandomListOfChannels(size);
        var videoCategories = VideoUtils.createRandomListOfVideoCategories(size);
        var videos = VideoUtils.createVideos(size, channels, videoCategories);
        var userVideos = VideoUtils.createListOfUserVideos(videos, user, Rating.LIKE);
        userVideoRepository.saveAll(userVideos);
        userVideoRepository.flush();
        return videos;
    }

}
