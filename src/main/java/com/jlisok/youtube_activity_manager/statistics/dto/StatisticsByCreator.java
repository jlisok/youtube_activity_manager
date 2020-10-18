package com.jlisok.youtube_activity_manager.statistics.dto;

public class StatisticsByCreator {

    private final String averageTime;
    private final String totalTime;
    private final Long numberVideos;
    private final String creatorName;


    public StatisticsByCreator(String averageTime, String totalTime, Long numberVideos, String creatorName) {
        this.averageTime = averageTime;
        this.totalTime = totalTime;
        this.numberVideos = numberVideos;
        this.creatorName = creatorName;
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

    public String getCreatorName() {
        return creatorName;
    }
}
