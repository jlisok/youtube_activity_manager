package com.jlisok.youtube_activity_manager.statistics.services;

import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCategory;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCreator;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsDto;

public interface StatisticsService {

    StatisticsDto<StatisticsByCategory> groupByCategory();

    StatisticsDto<StatisticsByCreator> groupByCreator();
}
