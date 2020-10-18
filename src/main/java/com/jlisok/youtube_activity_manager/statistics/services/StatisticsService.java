package com.jlisok.youtube_activity_manager.statistics.services;

import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCategory;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCreator;

import java.util.List;

public interface StatisticsService {

    List<StatisticsByCategory> groupByCategory();

    List<StatisticsByCreator> groupByCreator();
}
