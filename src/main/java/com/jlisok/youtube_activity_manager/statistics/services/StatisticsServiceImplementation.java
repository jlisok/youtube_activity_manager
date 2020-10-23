package com.jlisok.youtube_activity_manager.statistics.services;

import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCategory;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCreator;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsDto;
import com.jlisok.youtube_activity_manager.statistics.repositories.StatisticsByCreatorRepository;
import com.jlisok.youtube_activity_manager.statistics.repositories.StatisticsByVideoCategoryRepository;
import com.jlisok.youtube_activity_manager.synchronization.utils.SynchronizationStatusGetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.getAuthenticationInContext;

@Service
public class StatisticsServiceImplementation implements StatisticsService {

    private final StatisticsByVideoCategoryRepository repositoryByCategory;
    private final StatisticsByCreatorRepository repositoryByCreator;
    private final SynchronizationStatusGetter synchronizationStatusGetter;

    @Autowired
    public StatisticsServiceImplementation(StatisticsByVideoCategoryRepository repositoryByCategory, StatisticsByCreatorRepository repositoryByCreator, SynchronizationStatusGetter synchronizationStatusGetter) {
        this.repositoryByCategory = repositoryByCategory;
        this.repositoryByCreator = repositoryByCreator;
        this.synchronizationStatusGetter = synchronizationStatusGetter;
    }


    @Override
    public StatisticsDto<StatisticsByCategory> groupByCategory() {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        var stats = repositoryByCategory.groupByCategory(userId);
        var status = synchronizationStatusGetter.getLastSynchronization(userId);
        return new StatisticsDto<>(stats, status.getStatus(), status.getCreatedAt());
    }

    @Override
    public StatisticsDto<StatisticsByCreator> groupByCreator() {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        var stats = repositoryByCreator.groupByCreator(userId);
        var status = synchronizationStatusGetter.getLastSynchronization(userId);
        return new StatisticsDto<>(stats, status.getStatus(), status.getCreatedAt());
    }
}
