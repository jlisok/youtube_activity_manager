package com.jlisok.youtube_activity_manager.statistics.dto;


public class StatisticsByCategory {

    private final String averageTime;
    private final String totalTime;
    private final Long numberVideos;
    private final String categoryName;

    public StatisticsByCategory(String averageTime, String totalTime, Long numberVideos, String categoryName) {
        this.averageTime = averageTime;
        this.totalTime = totalTime;
        this.numberVideos = numberVideos;
        this.categoryName = categoryName;
    }

    public String getAverageTime() {
        return averageTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public Long getNumberVideos() {
        return numberVideos;
    }

    public String getCategoryName() {
        return categoryName;
    }
}