package com.jlisok.youtube_activity_manager.videos.repositories;

import com.jlisok.youtube_activity_manager.videos.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {

    List<Video> findAllByYouTubeVideoIdIn(Collection<String> youtubeId);
}
