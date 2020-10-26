package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
public interface TestUserVideoRepository extends JpaRepository<UserVideo, UUID> {

    @Transactional
    void deleteAllByUserId(UUID userId);
}
