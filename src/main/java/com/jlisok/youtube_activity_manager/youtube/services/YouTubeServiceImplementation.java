package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.api.YouTubeClient;
import com.jlisok.youtube_activity_manager.youtube.utils.IdsFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.youtube.constants.YouTubeApiClientRequest.*;

@Service
public class YouTubeServiceImplementation implements YouTubeService {

    private final YouTubeClient youTubeClient;
    private final VideoService videoService;
    private final UserVideoService userVideoService;
    private final ChannelService channelService;
    private final ChannelDatabaseService channelDatabaseService;
    private final VideoCategoryService videoCategoryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public YouTubeServiceImplementation(YouTubeClient youTubeClient, VideoService videoService, UserVideoService userVideoService, ChannelService channelService, ChannelDatabaseService channelDatabaseService, VideoCategoryService videoCategoryService) {
        this.youTubeClient = youTubeClient;
        this.videoService = videoService;
        this.userVideoService = userVideoService;
        this.channelService = channelService;
        this.channelDatabaseService = channelDatabaseService;
        this.videoCategoryService = videoCategoryService;
    }


    @Override
    @Transactional
    public void getSubscribedChannels(String accessToken, UUID userId) {
        List<Subscription> subscriptions = youTubeClient.fetchSubscriptions(accessToken, SUBSCRIPTION_REQUEST_PARTS, userId);
        List<String> youtubeChannelIds = IdsFetcher.getIdsFrom(subscriptions, subscription -> subscription.getSnippet()
                                                                                                          .getResourceId()
                                                                                                          .getChannelId());
        List<Channel> channels = getChannelsById(youtubeChannelIds, accessToken, userId);
        logger.debug("YouTubeService - fetching and inserting channels in database for userId {} - success.", userId);
    }


    @Override
    @Transactional
    public void getRatedVideos(String accessToken, UUID userId, Rating rating) {
        List<com.google.api.services.youtube.model.Video> youtubeVideos = youTubeClient.fetchRatedVideos(accessToken, VIDEO_REQUEST_PARTS, rating, userId);
        List<String> ytChannelIds = IdsFetcher.getIdsFrom(youtubeVideos, video -> video.getSnippet().getChannelId());
        List<String> ytCategoryIds = IdsFetcher.getIdsFrom(youtubeVideos, video -> video.getSnippet().getCategoryId());
        List<Channel> channels = getChannelsById(ytChannelIds, accessToken, null);
        List<VideoCategory> videoCategories = videoCategoryService.getVideoCategoriesByIds(accessToken, VIDEO_CATEGORY_REQUEST_PARTS, ytCategoryIds, userId);
        List<Video> videos = videoService.createVideos(youtubeVideos, channels, videoCategories);
        userVideoService.insertVideosVideoCategoriesAndChannels(videos, rating, userId);
        logger.debug("YouTubeService - fetching and inserting videos and related channels and videoCategories in database for userId {} - success.", userId);
    }


    private List<Channel> getChannelsById(List<String> ytChannelIds, String accessToken, UUID userId) {
        List<com.google.api.services.youtube.model.Channel> youtubeChannels = youTubeClient
                .fetchChannels(accessToken, CHANNEL_REQUEST_PARTS, ytChannelIds, userId);
        var channels = channelService.createChannels(youtubeChannels, userId);
        return channelDatabaseService.updateChannelsInDatabase(channels, userId);
    }
}


