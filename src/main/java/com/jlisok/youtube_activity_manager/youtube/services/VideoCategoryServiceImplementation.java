package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videoCategories.repositories.VideoCategoryRepository;
import com.jlisok.youtube_activity_manager.youtube.api.YouTubeClient;
import com.jlisok.youtube_activity_manager.youtube.utils.MapCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VideoCategoryServiceImplementation implements VideoCategoryService {

    private final YouTubeClient youTubeClient;
    private final VideoCategoryRepository repository;

    @Autowired
    public VideoCategoryServiceImplementation(YouTubeClient youTubeClient, VideoCategoryRepository repository) {
        this.youTubeClient = youTubeClient;
        this.repository = repository;
    }

    @Override
    @Transactional
    public List<VideoCategory> getVideoCategories(String accessToken, String parts, List<String> youtubeCategoryIds) {
        List<com.google.api.services.youtube.model.VideoCategory> ytVideoCategories = youTubeClient.fetchVideoCategories(accessToken, parts, youtubeCategoryIds);
        return createVideoCategories(ytVideoCategories, youtubeCategoryIds);
    }


    private List<VideoCategory> createVideoCategories(List<com.google.api.services.youtube.model.VideoCategory> ytCategories, List<String> youtubeCategoryIds) {
        Map<String, VideoCategory> dbVideoCategories = findDbVideoCategoriesBy(youtubeCategoryIds);
        return createVideoCategoryList(ytCategories, dbVideoCategories);
    }


    private Map<String, VideoCategory> findDbVideoCategoriesBy(List<String> youtubeCategoryIds) {
        List<VideoCategory> categories = repository.findAllByYoutubeIdIn(youtubeCategoryIds);
        return MapCreator.toMap(categories, VideoCategory::getYoutubeId, Function.identity());
    }


    private List<VideoCategory> createVideoCategoryList(List<com.google.api.services.youtube.model.VideoCategory> youtubeCategories, Map<String, VideoCategory> dbVideoCategories) {
        return youtubeCategories
                .stream()
                .map(category -> createOrReturnExisting(category, dbVideoCategories))
                .collect(Collectors.toList());
    }


    private VideoCategory createOrReturnExisting(com.google.api.services.youtube.model.VideoCategory category, Map<String, VideoCategory> dbVideoCategories) {
        return Optional.ofNullable(dbVideoCategories.get(category.getId()))
                       .orElse(createNew(category));
    }


    private VideoCategory createNew(com.google.api.services.youtube.model.VideoCategory category) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        return new VideoCategory(id, category.getSnippet().getTitle(), category.getId(), now, now);
    }
}
