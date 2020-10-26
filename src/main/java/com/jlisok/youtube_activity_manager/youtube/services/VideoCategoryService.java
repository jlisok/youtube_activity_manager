package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;

import java.util.List;
import java.util.UUID;

public interface VideoCategoryService {

    List<VideoCategory> getVideoCategoriesByIds(String accessToken, String videoCategoryRequestParts, List<String> categoryIds, UUID userId);

}
