package com.jlisok.youtube_activity_manager.videoCategories.repositories;

import com.google.api.services.youtube.model.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoCategoryRepository extends JpaRepository<VideoCategory, UUID> {
}
