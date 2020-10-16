package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.api.YouTubeClient;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;
import com.jlisok.youtube_activity_manager.youtube.utils.AccessTokenService;
import com.jlisok.youtube_activity_manager.youtube.utils.IdsFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.getAuthenticationInContext;
import static com.jlisok.youtube_activity_manager.youtube.constants.YouTubeApiClientRequest.*;

@Service
public class YouTubeServiceImplementation implements YouTubeService {

    private final AccessTokenService accessTokenService;
    private final YouTubeClient youTubeClient;
    private final VideoService videoService;
    private final UserVideoService userVideoService;
    private final ChannelService channelService;
    private final ChannelDatabaseService channelDatabaseService;
    private final VideoCategoryService videoCategoryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public YouTubeServiceImplementation(AccessTokenService accessTokenService, YouTubeClient youTubeClient, VideoService videoService, ChannelService channelService, UserVideoService userVideoService, ChannelDatabaseService channelDatabaseService, VideoCategoryService videoCategoryService) {
        this.videoCategoryService = videoCategoryService;
        logger.debug("YouTubeService - initialization.");
        this.channelDatabaseService = channelDatabaseService;
        this.channelService = channelService;
        this.videoService = videoService;
        this.userVideoService = userVideoService;
        this.accessTokenService = accessTokenService;
        this.youTubeClient = youTubeClient;
    }


    @Override
    @Transactional
    public List<Channel> listSubscribedChannels() {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        List<Subscription> subscriptions = youTubeClient.fetchSubscriptions(accessTokenService.getAccessToken(), SUBSCRIPTION_REQUEST_PARTS);
        if (subscriptions.isEmpty()) {
            logger.debug("YouTubeService - returning empty Channel list, userId {} has no subscriptions in YouTube - success.", userId);
            return Collections.emptyList();
        }
        List<String> youtubeChannelIds = IdsFetcher.getIdsFrom(subscriptions, subscription -> subscription.getSnippet()
                                                                                                          .getResourceId()
                                                                                                          .getChannelId());
        return getAndInsertChannels(youtubeChannelIds, userId);
    }


    @Override
    @Transactional
    public List<Video> listRatedVideos(YouTubeRatingDto dto) {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        List<com.google.api.services.youtube.model.Video> youtubeVideos = youTubeClient
                .fetchRatedVideos(accessTokenService.getAccessToken(), VIDEO_REQUEST_PARTS, dto.getRating());
        if (youtubeVideos.isEmpty()) {
            logger.debug("YouTubeService - returning empty Video list, userId {} has no subscriptions in YouTube - success.", userId);
            return Collections.emptyList();
        }
        List<String> ytChannelIds = IdsFetcher.getIdsFrom(youtubeVideos, video -> video.getSnippet().getChannelId());
        List<String> ytCategoryIds = IdsFetcher.getIdsFrom(youtubeVideos, video -> video.getSnippet().getCategoryId());
        List<Channel> channels = getAndInsertChannels(ytChannelIds, userId);
        List<VideoCategory> videoCategories = videoCategoryService.getVideoCategories(accessTokenService.getAccessToken(), VIDEO_CATEGORY_REQUEST_PARTS, ytCategoryIds);
        List<Video> videos = videoService.createVideos(youtubeVideos, channels, videoCategories);
        userVideoService.insertVideosInDatabase(videos, dto.getRating(), userId);
        logger.debug("YouTubeService - fetching and inserting videos in database for userId {} - success.", userId);
        return videos;
    }


    private List<Channel> getAndInsertChannels(List<String> ytChannelIds, UUID userId) {
        List<com.google.api.services.youtube.model.Channel> youtubeChannels = youTubeClient
                .fetchChannels(accessTokenService.getAccessToken(), CHANNEL_REQUEST_PARTS, ytChannelIds);
        List<Channel> channels = channelService.createChannels(youtubeChannels, userId);
        List<Channel> databaseChannels = channelDatabaseService.updateChannelsInDatabase(channels, userId);
        logger.debug("YouTubeService - fetching and inserting channels in database for userId {} - success.", userId);
        return databaseChannels;
    }
}


