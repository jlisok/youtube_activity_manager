package com.jlisok.youtube_activity_manager.youtube.constants;

public class YouTubeApiClientRequest {
    public static final String SUBSCRIPTION_REQUEST_PARTS = "snippet";
    public static final String VIDEO_REQUEST_PARTS = "snippet, contentDetails";
    public static final String CHANNEL_REQUEST_PARTS = "id, snippet, statistics, topicDetails, contentOwnerDetails";
    public static final String VIDEO_CATEGORY_REQUEST_PARTS = "id, snippet";
    public static final Integer MAX_ALLOWED_RESULTS_PER_PAGE = 50;
}
