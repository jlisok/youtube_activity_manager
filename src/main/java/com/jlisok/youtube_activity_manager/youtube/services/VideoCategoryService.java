package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;

import java.util.List;

public interface VideoCategoryService {

    List<VideoCategory> getVideoCategories(String accessToken, String videoCategoryRequestParts, List<String> categoryIds);

}
