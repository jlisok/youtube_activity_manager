package com.jlisok.youtube_activity_manager.statistics.repositories;

import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCreator;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatisticsByCreatorRepository extends JpaRepository<Video, UUID> {

    // joins made manually. User-Video entities are connected through two paths, via channels and via UserVideos.
    // hibernate gets confused when not given explicit route of relation to build query upon
    @Query(value = "SELECT " +
            "new com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCreator(TO_CHAR(AVG(v.duration),'HH24:MI:SS'), TO_CHAR(SUM(v.duration),'DD:HH24:MI:SS'), COUNT(v.id), v.channel.title) " +
            "FROM Video v " +
            "JOIN UserVideo uv ON v.id = uv.video.id " +
            "JOIN User u ON u.id = uv.user.id " +
            "JOIN v.channel c " +
            "WHERE u.id = :id " +
            "GROUP BY c.title " +
            "ORDER BY c.title")
    List<StatisticsByCreator> groupByCreator(UUID id);
}
