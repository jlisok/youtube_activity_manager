package com.jlisok.youtube_activity_manager.statistics.controllers;

import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCategory;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsByCreator;
import com.jlisok.youtube_activity_manager.statistics.dto.StatisticsDto;
import com.jlisok.youtube_activity_manager.statistics.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/statistics")
public class StatisticsController {

    private final StatisticsService service;

    @Autowired
    public StatisticsController(StatisticsService service) {
        this.service = service;
    }

    @GetMapping("/category")
    public ResponseEntity<StatisticsDto<StatisticsByCategory>> getStatsGroupedByCategory() {
        return ResponseEntity
                .ok()
                .body(service.groupByCategory());
    }

    @GetMapping("/creator")
    public ResponseEntity<StatisticsDto<StatisticsByCreator>> getStatsGroupedByCreator() {
        return ResponseEntity
                .ok()
                .body(service.groupByCreator());
    }
}
