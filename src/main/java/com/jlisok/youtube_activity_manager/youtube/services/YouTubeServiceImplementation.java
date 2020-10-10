package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.channel.models.Channel;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.api.YouTubeClient;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;
import com.jlisok.youtube_activity_manager.youtube.utils.AccessTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.getAuthenticationInContext;
import static com.jlisok.youtube_activity_manager.youtube.constants.YouTubeApiClientRequestParts.*;

@Service
public class YouTubeServiceImplementation implements YouTubeService {

    private final AccessTokenService accessTokenService;
    private final YouTubeClient youTubeClient;
    private final VideoService videoService;
    private final UserVideoService userVideoService;
    private final SubscriptionService subscriptionService;
    private final ChannelService channelService;
    private final ChannelDatabaseService channelDatabaseService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public YouTubeServiceImplementation(AccessTokenService accessTokenService, YouTubeClient youTubeClient, VideoService videoService, ChannelService channelService, UserVideoService userVideoService, SubscriptionService subscriptionService, ChannelDatabaseService channelDatabaseService) {
        logger.debug("YouTubeService - initialization.");
        this.subscriptionService = subscriptionService;
        this.channelDatabaseService = channelDatabaseService;
        this.channelService = channelService;
        this.videoService = videoService;
        this.userVideoService = userVideoService;
        this.accessTokenService = accessTokenService;
        this.youTubeClient = youTubeClient;
    }


    @Override
    public List<Channel> listSubscribedChannels() throws IOException {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        List<Subscription> subscriptions = youTubeClient.fetchSubscriptions(accessTokenService.getAccessToken(), SUBSCRIPTION_REQUEST_PARTS);
        if (subscriptions.isEmpty()) {
            logger.debug("YouTubeService - returning empty Channel list, userId {} has no subscriptions in YouTube - success.", userId);
            return Collections.emptyList();
        }
        List<String> youtubeChannelIds = subscriptionService.retrieveChannelId(subscriptions);
        List<com.google.api.services.youtube.model.Channel> youtubeChannels = youTubeClient
                .fetchChannels(accessTokenService.getAccessToken(), CHANNEL_REQUEST_PARTS, youtubeChannelIds);
        logger.debug("YouTubeService - fetching subscribed channels for userId {} - success.", userId);
        List<Channel> channels = channelService.createChannels(youtubeChannels, userId);
        channelDatabaseService.updateChannelsInDatabase(channels, userId);
        logger.debug("YouTubeService - inserting channels in database for userId {} - success.", userId);
        return channels;
    }


    @Override
    public List<Video> listRatedVideos(YouTubeRatingDto dto) throws IOException {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        List<com.google.api.services.youtube.model.Video> videos = youTubeClient
                .fetchRatedVideos(accessTokenService.getAccessToken(), VIDEO_REQUEST_PARTS, dto.getRating());
        if (videos.isEmpty()) {
            logger.debug("YouTubeService - returning empty Video list, userId {} has no subscriptions in YouTube - success.", userId);
            return new ArrayList<>(0);
        }
        logger.debug("YouTubeService - fetching list of rated videos for userId {} - success.", userId);
        List<Video> videoList = videoService.createVideos(videos);
        userVideoService.insertVideosInDatabase(videoList, dto.getRating(), userId);
        logger.debug("YouTubeService - inserting videos in database for userId {} - success.", userId);
        return videoList;
    }
}


