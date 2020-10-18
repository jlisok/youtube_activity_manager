package com.jlisok.youtube_activity_manager.statistics.repositories;

import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCategory;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatisticsByVideoCategoryRepository extends JpaRepository<UserVideo, UUID> {

    @Query(value = "SELECT " +
            "new com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCategory(TO_CHAR(AVG(uv.video.duration),'HH24:MI:SS'), TO_CHAR(SUM(uv.video.duration),'DD:HH24:MI:SS'), COUNT(uv.video.id), uv.video.videoCategory.categoryName) " +
            "FROM UserVideo uv " +
            "WHERE uv.user.id = :id " +
            "GROUP BY uv.video.videoCategory.categoryName " +
            "ORDER BY uv.video.videoCategory.categoryName")
    List<StatisticsByCategory> groupByCategory(UUID id);
}
