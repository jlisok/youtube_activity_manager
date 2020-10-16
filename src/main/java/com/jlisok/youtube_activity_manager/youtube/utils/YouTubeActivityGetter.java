package com.jlisok.youtube_activity_manager.youtube.utils;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.VideoCategoryListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.youtube.exceptions.YouTubeApiException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.youtube.constants.YouTubeApiClientRequest.MAX_ALLOWED_RESULTS_PER_PAGE;

@Component
public class YouTubeActivityGetter {


    public SubscriptionListResponse getYouTubeSubscriptions(YouTube youTube, String parts, String pageToken) {
        try {
            return youTube
                    .subscriptions()
                    .list(parts)
                    .setMine(true)
                    .setPageToken(pageToken)
                    .setMaxResults(MAX_ALLOWED_RESULTS_PER_PAGE.longValue())
                    .execute();
        } catch (IOException e) {
            UUID userId = JwtAuthenticationContext.getAuthenticationInContext().getAuthenticatedUserId();
            throw new YouTubeApiException("Failed while connecting to YouTubeApi, userId " + userId + " Bad request.", e);
        }
    }


    public ChannelListResponse getYouTubeChannels(YouTube youTube, String parts, String inputIds) {
        try {
            return youTube
                    .channels()
                    .list(parts)
                    .setId(inputIds)
                    .setMaxResults(MAX_ALLOWED_RESULTS_PER_PAGE.longValue())
                    .execute();
        } catch (IOException e) {
            UUID userId = JwtAuthenticationContext.getAuthenticationInContext().getAuthenticatedUserId();
            throw new YouTubeApiException("Failed while connecting to YouTubeApi, userId " + userId + " Bad request.", e);
        }
    }


    public VideoCategoryListResponse getYouTubeVideoCategories(YouTube youTube, String parts, String inputIds) {
        try {
            return youTube
                    .videoCategories()
                    .list(parts)
                    .setId(inputIds)
                    .execute();
        } catch (IOException e) {
            UUID userId = JwtAuthenticationContext.getAuthenticationInContext().getAuthenticatedUserId();
            throw new YouTubeApiException("Failed while connecting to YouTubeApi, userId " + userId + " Bad request.", e);
        }
    }


    public VideoListResponse getYouTubeVideos(YouTube youTube, String parts, Rating rating, String pageToken) {
        try {
            return youTube
                    .videos()
                    .list(parts)
                    .setMyRating(rating.toString().toLowerCase())
                    .setPageToken(pageToken)
                    .setMaxResults(MAX_ALLOWED_RESULTS_PER_PAGE.longValue())
                    .execute();
        } catch (IOException e) {
            UUID userId = JwtAuthenticationContext.getAuthenticationInContext().getAuthenticatedUserId();
            throw new YouTubeApiException("Failed while connecting to YouTubeApi, userId " + userId + " Bad request.", e);
        }
    }
}
