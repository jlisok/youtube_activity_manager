package com.jlisok.youtube_activity_manager.videos.repositories;

import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserVideoRepository extends JpaRepository<UserVideo, UUID> {

    List<UserVideo> findByUserIdAndRating(UUID userId, Rating rating);

    List<UserVideo> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Transactional
    void deleteAllByUserId(UUID userId);

}
