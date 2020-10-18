package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class YouTubeActivityUtils implements TestProfile {

    @Autowired
    private UserVideoRepository userVideoRepository;


    @Transactional
    public List<Video> insertUsersYouTubeActivity(User user) {
        List<com.google.api.services.youtube.model.Channel> youtubeChannels = ChannelAndSubscriptionUtils.createRandomYouTubeChannelList(20);
        int size = youtubeChannels.size();
        List<VideoCategory> videoCategories = VideoUtils.createRandomListOfVideoCategories(size);
        List<com.google.api.services.youtube.model.Video> youtubeVideos = VideoUtils.createYouTubeVideoListGivenChannelsAndVideoCategory(40, youtubeChannels, videoCategories);
        List<Channel> channels = ChannelAndSubscriptionUtils.createListOfChannelsFromYouTubeChannels(youtubeChannels, user);
        List<Video> videos = VideoUtils.createListOfVideosFromYouTubeVideos(youtubeVideos, channels, videoCategories);
        List<UserVideo> userVideos = videos.stream()
                                           .map(video -> VideoUtils.createUserVideo(video, user, Rating.LIKE))
                                           .collect(Collectors.toList());
        userVideoRepository.saveAll(userVideos);
        userVideoRepository.flush();
        return videos;
    }

}
