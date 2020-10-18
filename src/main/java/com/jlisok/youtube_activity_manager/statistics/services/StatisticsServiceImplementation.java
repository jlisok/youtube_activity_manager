package com.jlisok.youtube_activity_manager.statistics.services;

import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCategory;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCreator;
import com.jlisok.youtube_activity_manager.statistics.repositories.StatisticsByCreatorRepository;
import com.jlisok.youtube_activity_manager.statistics.repositories.StatisticsByVideoCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.getAuthenticationInContext;

@Service
public class StatisticsServiceImplementation implements StatisticsService {

    private final StatisticsByVideoCategoryRepository repositoryByCategory;
    private final StatisticsByCreatorRepository repositoryByCreator;

    @Autowired
    public StatisticsServiceImplementation(StatisticsByVideoCategoryRepository repositoryByCategory, StatisticsByCreatorRepository repositoryByCreator) {
        this.repositoryByCategory = repositoryByCategory;
        this.repositoryByCreator = repositoryByCreator;
    }


    @Override
    public List<StatisticsByCategory> groupByCategory() {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        return repositoryByCategory.groupByCategory(userId);
    }

    @Override
    public List<StatisticsByCreator> groupByCreator() {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        return repositoryByCreator.groupByCreator(userId);
    }
}
