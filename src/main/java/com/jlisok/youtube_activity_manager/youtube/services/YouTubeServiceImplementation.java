package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
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
import java.util.List;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.getAuthenticationInContext;

@Service
public class YouTubeServiceImplementation implements YouTubeService {

    private final AccessTokenService accessTokenService;
    private final YouTubeClient youTubeClient;
    private final VideoService videoService;
    private final UserVideoService userVideoService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String requestParts = "snippet, contentDetails";


    @Autowired
    public YouTubeServiceImplementation(AccessTokenService accessTokenService, YouTubeClient youTubeClient, VideoService videoService, UserVideoService userVideoService) {
        logger.debug("YouTubeService - initialization.");
        this.videoService = videoService;
        this.userVideoService = userVideoService;
        this.accessTokenService = accessTokenService;
        this.youTubeClient = youTubeClient;
    }


    //TODO: [WIP] full functionality resembling the flow introduced in listRatedVideo not implemented yet but will be within the next commit
    @Override
    public List<Subscription> listSubscribedChannels() throws IOException {
        List<Subscription> subscriptions = youTubeClient.fetchSubscribedChannels(accessTokenService.getAccessToken(), requestParts);
        logger.debug("YouTubeService - fetching list of subscribed channels for userId {} - success.", getAuthenticationInContext()
                .getAuthenticatedUserId());
        return subscriptions;
    }


    @Override
    public List<Video> listRatedVideos(YouTubeRatingDto dto) throws IOException, ExpectedDataNotFoundInDatabase {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        List<com.google.api.services.youtube.model.Video> videos = youTubeClient.fetchRatedVideos(accessTokenService.getAccessToken(), requestParts, dto
                .getRating());
        logger.debug("YouTubeService - fetching list of rated videos for userId {} - success.", userId);
        if (videos.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<Video> videoList = videoService.createListOfVideos(videos);
        userVideoService.insertListInDatabase(videoList, dto.getRating(), userId);
        logger.debug("YouTubeService - inserting videos in database for userId {} - success.", userId);
        return videoList;
    }
}


