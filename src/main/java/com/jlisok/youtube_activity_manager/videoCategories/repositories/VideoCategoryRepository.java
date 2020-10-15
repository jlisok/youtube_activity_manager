package com.jlisok.youtube_activity_manager.videoCategories.repositories;

import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface VideoCategoryRepository extends JpaRepository<VideoCategory, UUID> {

    List<VideoCategory> findAllByYoutubeIdIn(Collection<String> youtubeIds);
}
