package com.jlisok.youtube_activity_manager.videos.repositories;

import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserVideoRepository extends JpaRepository<UserVideo, UUID> {

    List<UserVideo> findByUserId(UUID userId);

}
